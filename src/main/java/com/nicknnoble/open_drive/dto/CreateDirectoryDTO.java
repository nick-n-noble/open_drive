package com.nicknnoble.open_drive.dto;

import lombok.Data;

@Data
public class CreateDirectoryDTO {
    private String dirName;
    private String parentDir;
}
