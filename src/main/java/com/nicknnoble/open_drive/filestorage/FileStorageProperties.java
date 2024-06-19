package com.nicknnoble.open_drive.filestorage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

    private String uploadDir;
}
