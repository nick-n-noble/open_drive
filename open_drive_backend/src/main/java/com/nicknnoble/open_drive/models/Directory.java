package com.nicknnoble.open_drive.models;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class Directory {

    private String name;
    private Map<String, Directory> directories;
    private Map<String, FileEntry> files;

    

    public Directory(String name) {
        this.name = name;
        this.directories = new HashMap<String, Directory>();
        this.files = new HashMap<String, FileEntry>();
    }
}
