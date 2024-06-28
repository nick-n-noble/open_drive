package com.nicknnoble.open_drive.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadFileDTO {
    private MultipartFile file;
    private String parentDir;
}
