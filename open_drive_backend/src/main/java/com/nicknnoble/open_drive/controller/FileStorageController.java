package com.nicknnoble.open_drive.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nicknnoble.open_drive.dto.CreateDirectoryDTO;
import com.nicknnoble.open_drive.dto.DeleteFileDTO;
import com.nicknnoble.open_drive.dto.DownloadFileDTO;
import com.nicknnoble.open_drive.dto.FileResponseDTO;
import com.nicknnoble.open_drive.dto.UploadFileDTO;
import com.nicknnoble.open_drive.dto.UploadMultipleFilesDTO;
import com.nicknnoble.open_drive.filestorage.FileNotFoundException;
import com.nicknnoble.open_drive.filestorage.FileStorageException;
import com.nicknnoble.open_drive.service.FileStorageService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/file")
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("createDir")
    public ResponseEntity<String> createDir(CreateDirectoryDTO createDirectoryDTO, HttpServletRequest request) {

        try {
            String dir = fileStorageService.createDirectory(createDirectoryDTO.getDirName(), createDirectoryDTO.getParentDir(), request);
            return new ResponseEntity<String>(dir + " created", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("uploadFile")
    public ResponseEntity<?> uploadFile(UploadFileDTO uploadFileDTO, HttpServletRequest request) {
        
        MultipartFile file = uploadFileDTO.getFile();

        try {
            String fileName = fileStorageService.storeFile(file, uploadFileDTO.getParentDir(), request);
            FileResponseDTO fileResponse = new FileResponseDTO(fileName, file.getContentType(), file.getSize());
            return new ResponseEntity<FileResponseDTO>(fileResponse, HttpStatus.OK);
        } catch (FileStorageException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("uploadMultipleFiles")
    public List<ResponseEntity<?>> uploadMultipleFiles(UploadMultipleFilesDTO uploadMultipleFilesDTO, HttpServletRequest request) {

        MultipartFile[] files = uploadMultipleFilesDTO.getFiles();
        String parentDir = uploadMultipleFilesDTO.getParentDir();

        return Arrays.asList(files)
            .stream()
            .map(file -> uploadFile(new UploadFileDTO(file, parentDir), request))
            .collect(Collectors.toList());
    }

    @GetMapping("downloadFile")
    public ResponseEntity<?> downloadFile(DownloadFileDTO downloadFileDTO, HttpServletRequest request) {
        Logger logger = LoggerFactory.getLogger(FileStorageController.class);
        try {
            Resource resource = fileStorageService.loadFileAsResource(downloadFileDTO.getFilePath(), request);

            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException e) {
                logger.info("Could not determine file type");
            }

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        
    }

    @PostMapping("deleteDirectory")
    public ResponseEntity<String> deleteDirectory(DeleteFileDTO deleteFileDTO, HttpServletRequest request) {
        try {
            String dir = fileStorageService.deleteDirectory(deleteFileDTO.getPath(), request);
            return new ResponseEntity<String>(dir + " deleted", HttpStatus.OK);
        } catch (FileStorageException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("deleteFile")
    public ResponseEntity<String> deleteFile(DeleteFileDTO deleteFileDTO, HttpServletRequest request) {
        try {
            String file = fileStorageService.deleteFile(deleteFileDTO.getPath(), request);
            return new ResponseEntity<String>(file + " deleted", HttpStatus.OK);
        } catch (FileStorageException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
