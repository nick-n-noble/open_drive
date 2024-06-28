package com.nicknnoble.open_drive.security;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day for testing purposes
    // This will change every time server is started for testing purposes
    public static final SecretKey SECRET_KEY = Jwts.SIG.HS512.key().build();
}
