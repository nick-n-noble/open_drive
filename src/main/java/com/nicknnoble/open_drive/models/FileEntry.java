package com.nicknnoble.open_drive.models;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Data
@Table("files")
public class FileEntry {
    @PrimaryKey
    private UUID fileId;

   
    private UUID ownerId;

 
    private UUID parentDirectory;

    private String serverFilePath;

    
}
