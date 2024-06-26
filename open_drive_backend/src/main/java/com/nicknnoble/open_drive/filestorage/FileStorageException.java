package com.nicknnoble.open_drive.filestorage;

public class FileStorageException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
