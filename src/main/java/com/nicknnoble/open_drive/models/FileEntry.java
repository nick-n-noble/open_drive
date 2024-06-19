package com.nicknnoble.open_drive.models;

import lombok.Data;

@Data
public class FileEntry {

    private final String fileName;
    private final String serverFilePath;
}
