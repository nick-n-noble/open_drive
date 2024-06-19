package com.nicknnoble.open_drive.models;

import java.util.List;

import lombok.Data;

@Data
public class Directory {

    private String name;
    private String directoryFilePath;
    private List<Directory> directories;
    private List<FileEntry> files;
}
