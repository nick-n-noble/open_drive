package com.nicknnoble.open_drive.security;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JWTGenerator {

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(SecurityConstants.SECRET_KEY)
                .compact();
        
        return token;
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(SecurityConstants.SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.getSubject();      
    }

    public boolean validateToken(String token) throws JwtException {
        try {
            Jwts.parser().verifyWith(SecurityConstants.SECRET_KEY).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            throw new JwtException("JWT was expired or incorrect", e);
        }
    }
}
