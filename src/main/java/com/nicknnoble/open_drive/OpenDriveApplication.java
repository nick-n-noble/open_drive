package com.nicknnoble.open_drive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nicknnoble.open_drive.security.SecurityConstants;

@SpringBootApplication
public class OpenDriveApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenDriveApplication.class, args);
	}
}
