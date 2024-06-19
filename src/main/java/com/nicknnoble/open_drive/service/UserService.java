package com.nicknnoble.open_drive.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nicknnoble.open_drive.models.UserEntity;
import com.nicknnoble.open_drive.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UUID getIdByUsername(String username) {
        Optional <UserEntity> user = userRepository.findByUsername(username);
        return user.map(UserEntity::getId).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
