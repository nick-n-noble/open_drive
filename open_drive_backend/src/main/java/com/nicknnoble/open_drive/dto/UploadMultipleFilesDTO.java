package com.nicknnoble.open_drive.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UploadMultipleFilesDTO {
    private MultipartFile[] files;
    private String parentDir;
}
