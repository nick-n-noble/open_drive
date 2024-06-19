package com.nicknnoble.open_drive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nicknnoble.open_drive.security.SecurityConstants;

@SpringBootApplication
public class AuthCassandraApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthCassandraApplication.class, args);
        System.out.println("SECRET KEY: " + SecurityConstants.SECRET_KEY);
	}
}
