package com.nicknnoble.open_drive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileResponseDto {
   
    private String fileName;
    private String fileType;
    private long size;
}
