package com.nicknnoble.open_drive.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.nicknnoble.open_drive.filestorage.FileNotFoundException;
import com.nicknnoble.open_drive.filestorage.FileStorageException;
import com.nicknnoble.open_drive.filestorage.FileStorageProperties;
import com.nicknnoble.open_drive.models.Directory;
import com.nicknnoble.open_drive.models.FileEntry;
import com.nicknnoble.open_drive.models.UserEntity;
import com.nicknnoble.open_drive.repository.UserRepository;
import com.nicknnoble.open_drive.security.JWTAuthenticationFilter;
import com.nicknnoble.open_drive.security.JwtGenerator;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired 
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserRepository userRepository;

    public FileStorageService(FileStorageProperties FileStorageProperties) throws FileStorageException {
        
        this.fileStorageLocation = Paths.get(FileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    public String getUserDirFromRequest(HttpServletRequest request) {
        String token = jwtAuthenticationFilter.getJWTFromRequest(request);
        String username = jwtGenerator.getUsernameFromJwt(token);
        String userIdDir = userService.getIdByUsername(username).toString();

        return userIdDir;
    }

    public void createNewUserDirectory(String id) throws FileStorageException {
        try {
            Path path = fileStorageLocation.resolve(id);
            Files.createDirectories(path);
        } catch (Exception e) {
            throw new FileStorageException("User directory could not be created: " + e.getMessage(), e);
        }
    }

    public String createDirectory(String dirName, String parentDir, HttpServletRequest request) throws FileStorageException {
        
        if (!parentDir.isEmpty() && !isValidDirectoryPath(parentDir)) {
            throw new FileStorageException(parentDir + " is not a valid path");
        }

        final String USER_DIR = getUserDirFromRequest(request);
        String serverDir = USER_DIR + '/' + parentDir; 

        if (!isValidFileName(dirName)) {
            throw new FileStorageException("Invalid directory name: " + dirName);
        }
        try {
            Path parentDirPath = fileStorageLocation.resolve(serverDir);
            if(!Files.exists(parentDirPath)) {
                throw new FileStorageException("Parent directory " + serverDir + " does not exist");
            }

            Path newDirPath = parentDirPath.resolve(dirName);
            if (Files.exists(newDirPath)) {
                throw new FileStorageException(dirName + " already exists in parent directory " + parentDir);
            }

            UserEntity user = userService.getUserFromRequest(request);
            user.addDirectory(dirName, parentDir);

            userRepository.save(user);

            Files.createDirectories(newDirPath);
            return newDirPath.toString();
        } catch (IOException e) {
            throw new FileStorageException("Could not create directory " + dirName + " in parent directory " + parentDir, e);
        }
    }

    public String storeFile(MultipartFile file, String parentDir, HttpServletRequest request) throws FileStorageException {

        if (!parentDir.isEmpty() && !isValidDirectoryPath(parentDir)) {
            throw new FileStorageException(parentDir + " is not a valid path");
        }

        final String USER_DIR = getUserDirFromRequest(request);
        String uploadDir = USER_DIR + '/' + parentDir; 

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null) {
            throw new FileStorageException("Failed to store file with a null filename.");
        }
        
        // Normalize file name
        String fileName = StringUtils.cleanPath(originalFilename);

        if (!isValidFileName(fileName)) {
            throw new FileStorageException("Invalid file name: " + fileName);
        }

        Path dirPath = fileStorageLocation.resolve(uploadDir);

        if(!Files.exists(dirPath)) {
            throw new FileStorageException("Directory " + parentDir + " does not exist");
        }

        try {
            Path targetLocation = dirPath.resolve(fileName);

            if (Files.exists(targetLocation)) {
                throw new FileStorageException(fileName + " already exists in parent directory " + parentDir);
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            UserEntity user = userService.getUserFromRequest(request);
            Directory userDirectory = user.getDirectoryByPathString(parentDir);
            
            if (userDirectory == null) {
                user.getFiles().add(new FileEntry(fileName, parentDir + fileName));
            }

            else {
                userDirectory.getFiles().add(new FileEntry(fileName, parentDir + fileName));
            }
            

            userRepository.save(user);
            return fileName;

        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + fileName + ": " + e.getMessage(), e);
        }
    }

    public Resource loadFileAsResource(String filePath, HttpServletRequest request) throws FileNotFoundException{

        if (!isValidFilePath(filePath)) {
            throw new FileNotFoundException(filePath + " is not a valid path");
        }

        final String USER_DIR = getUserDirFromRequest(request);
        String downloadDir = USER_DIR + '/' + filePath; 

        try {
            Path serverFilePath = this.fileStorageLocation.resolve(downloadDir).normalize();
            Resource resource = new UrlResource(serverFilePath.toUri());

            if (!resource.exists()) {
                throw new FileNotFoundException("File not found " + filePath);
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new FileNotFoundException("File not found " + filePath, e);
        }
    }

    public static boolean isValidFileName(String name) {
        // Regex to match valid directory names (no special characters that are not allowed)
        String regex = "^[^/\\\\|?*<>:\"&]+$";
        return name != null && !name.isEmpty() && name.matches(regex) && !name.contains("..") && name.length() < 255;
    }

    public static boolean isValidDirectoryPath(String path) {
        String regex = "^(?!\\/)(?!.*(?:^|\\/|\\.)\\/\\.{1,2}(?:\\/|$))(?!.*(?:^|\\/|\\.)\\.{1,2}$)[A-Za-z0-9_\\-\\.]+(?:\\/[A-Za-z0-9_\\-\\.]+)*\\/$";        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }

    public static boolean isValidFilePath(String path) {
        String regex = "^(?!\\/)(?!.*(?:^|\\/|\\.)\\/\\.{1,2}(?:\\/|$))(?!.*(?:^|\\/|\\.)\\.{1,2}$)[A-Za-z0-9_\\-\\.]+(?:\\/[A-Za-z0-9_\\-\\.]+)*$";        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }

}
