package com.nicknnoble.open_drive.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.nicknnoble.open_drive.filestorage.FileNotFoundException;
import com.nicknnoble.open_drive.filestorage.FileStorageException;
import com.nicknnoble.open_drive.filestorage.FileStorageProperties;
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
        //System.out.println("FILE STORAGE PATH STRING FOR REFERENCE: " + fileStorageLocation.toString());
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

    public String createDirectory(String dirName, String parentDir) throws FileStorageException {
        if (!isValidFileName(dirName)) {
            throw new FileStorageException("Invalid directory name: " + dirName);
        }
        try {
            Path parentDirPath = fileStorageLocation.resolve(parentDir);
            if(!Files.exists(parentDirPath)) {
                throw new FileStorageException("Parent directory " + parentDir + " does not exist");
            }

            Path newDirPath = parentDirPath.resolve(dirName);
            if (Files.exists(newDirPath) && Files.isDirectory(newDirPath)) {
                // Test this again, does not display in response.
                throw new FileStorageException(dirName + " already exists in parent directory " + parentDir);
            }
            Files.createDirectories(newDirPath);
            return newDirPath.toString();
        } catch (IOException e) {
            throw new FileStorageException("Could not create directory " + dirName + " in parent directory " + parentDir, e);
        }
    }

    public static boolean isValidFileName(String name) {
        // Regex to match valid directory names (no special characters that are not allowed)
        String regex = "^[^/\\\\|?*<>:\"&]+$";
        return name != null && !name.isEmpty() && name.matches(regex) && !name.contains("..") && name.length() < 255;
    }

    public String storeFile(MultipartFile file, String dir, HttpServletRequest request) throws FileStorageException {

        final String USER_DIR = getUserDirFromRequest(request);
        String uploadDir = USER_DIR + '/' + dir; 
        System.out.println("UPLOAD DIR: " + uploadDir);

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
            throw new FileStorageException("Directory " + dir + " does not exist");
        }

        try {
            Path targetLocation = dirPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            UserEntity user = userService.getUserFromRequest(request);
            user.getFiles().add(new FileEntry(fileName, dir + fileName));

            userRepository.save(user);
            return fileName;

        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + fileName, e);
        }
    }

    public Resource loadFileAsResource(String fileLocation, HttpServletRequest request) throws FileNotFoundException{

        final String USER_DIR = getUserDirFromRequest(request);
        String downloadDir = USER_DIR + '/' + fileLocation; 
        System.out.println("DOWNLOAD DIR: " + downloadDir);

        try {
            Path filePath = this.fileStorageLocation.resolve(downloadDir).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new FileNotFoundException("File not found " + fileLocation);
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new FileNotFoundException("File not found " + fileLocation, e);
        }
    }


}
