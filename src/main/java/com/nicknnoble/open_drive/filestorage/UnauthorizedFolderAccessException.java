package com.nicknnoble.open_drive.filestorage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedFolderAccessException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnauthorizedFolderAccessException(String message) {
        super(message);
    }

    public UnauthorizedFolderAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
