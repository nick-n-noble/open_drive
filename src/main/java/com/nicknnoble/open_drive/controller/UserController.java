package com.nicknnoble.open_drive.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nicknnoble.open_drive.models.UserEntity;
import com.nicknnoble.open_drive.repository.UserRepository;

@RestController
@RequestMapping(path="/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;
    
    @GetMapping()
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{username}")
    public Optional<UserEntity> getUserByUsername(@PathVariable("username") String username) {
        return userRepository.findByUsername(username);
    }

    
}
