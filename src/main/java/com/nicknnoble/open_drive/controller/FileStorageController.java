package com.nicknnoble.open_drive.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nicknnoble.open_drive.dto.FileResponseDto;
import com.nicknnoble.open_drive.filestorage.FileNotFoundException;
import com.nicknnoble.open_drive.filestorage.FileStorageException;
import com.nicknnoble.open_drive.service.FileStorageService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/file")
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("create-dir")
    public ResponseEntity<?> createDir(@RequestParam("name") String dirName, @RequestParam("parent") String parentDir, HttpServletRequest request) {
        
        final String USER_DIR = fileStorageService.getUserDirFromRequest(request);
        parentDir = USER_DIR + '/' + parentDir; 
        System.out.println("PARENT DIR: " + parentDir);

        try {
            String dir = fileStorageService.createDirectory(dirName, parentDir);
            return new ResponseEntity<String>(dir, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam String dir, HttpServletRequest request) {
        final String USER_DIR = fileStorageService.getUserDirFromRequest(request);
        String uploadDir = USER_DIR + '/' + dir; 
        System.out.println("UPLOAD DIR: " + uploadDir);
        try {
            String fileName = fileStorageService.storeFile(file, uploadDir);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/file/download/")
                .path(fileName)
                .toUriString();

            FileResponseDto fileResponse = new FileResponseDto(fileName, fileDownloadUri, file.getContentType(), file.getSize());
            return new ResponseEntity<FileResponseDto>(fileResponse, HttpStatus.OK);
        } catch (FileStorageException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // @PostMapping("uploadMultiple")
    // public List<ResponseEntity<?>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
    //     return Arrays.asList(files)
    //         .stream()
    //         .map(file -> uploadFile(file))
    //         .collect(Collectors.toList());
    // }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Logger logger = LoggerFactory.getLogger(FileStorageController.class);
        try {
            Resource resource = fileStorageService.loadFileAsResource(fileName);

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
}
