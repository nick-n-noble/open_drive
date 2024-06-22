package com.nicknnoble.open_drive.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UploadFileDTO {
    private MultipartFile file;
    private String parentDir;
}
