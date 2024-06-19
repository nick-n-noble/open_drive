package com.nicknnoble.open_drive.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nicknnoble.open_drive.models.UserEntity;
import com.nicknnoble.open_drive.repository.UserRepository;
import com.nicknnoble.open_drive.security.JWTAuthenticationFilter;
import com.nicknnoble.open_drive.security.JwtGenerator;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtGenerator jwtGenerator;

    public UUID getIdByUsername(String username) {
        Optional <UserEntity> user = userRepository.findByUsername(username);
        return user.map(UserEntity::getId).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public UserEntity getUserFromRequest(HttpServletRequest request) {
        String token = jwtAuthenticationFilter.getJWTFromRequest(request);
        String username = jwtGenerator.getUsernameFromJwt(token);
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
    }
}
