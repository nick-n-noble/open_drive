package com.nicknnoble.open_drive.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Directory {

    private String name;
    private Map<String, Directory> directories;
    private List<FileEntry> files;

    

    public Directory(String name) {
        this.name = name;
        this.directories = new HashMap<String, Directory>();
        this.files = new LinkedList<FileEntry>();
    }
}
