package com.nicknnoble.open_drive.models;

import java.util.UUID;

import lombok.Data;

@Data
public class FileEntry {

    private UUID fileId;

    private UUID ownerId;

    private UUID parentDirectory;

    private String serverFilePath;
}
