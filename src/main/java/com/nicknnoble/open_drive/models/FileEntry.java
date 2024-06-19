package com.nicknnoble.open_drive.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileEntry {

    private String fileName;
    private String serverFilePath;
}
