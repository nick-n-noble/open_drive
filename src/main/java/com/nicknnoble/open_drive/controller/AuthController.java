package com.nicknnoble.open_drive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nicknnoble.open_drive.dto.AuthResponseDto;
import com.nicknnoble.open_drive.dto.LoginDto;
import com.nicknnoble.open_drive.dto.RegisterDto;
import com.nicknnoble.open_drive.models.Role;
import com.nicknnoble.open_drive.models.UserEntity;
import com.nicknnoble.open_drive.repository.UserRepository;
import com.nicknnoble.open_drive.security.JwtGenerator;
import com.nicknnoble.open_drive.service.FileStorageService;
import com.nicknnoble.open_drive.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired 
    private UserService userService;
    
    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(LoginDto loginDto) {
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<AuthResponseDto>(new AuthResponseDto(token) , HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<String> register(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<String>("Username is taken.", HttpStatus.BAD_REQUEST);
        }

        String encryptedPassword = passwordEncoder.encode(registerDto.getPassword());

        UserEntity user = new UserEntity(registerDto.getUsername(), encryptedPassword, Role.USER);

        userRepository.save(user);

        try {
            fileStorageService.createNewUserDirectory(userService.getIdByUsername(user.getUsername()).toString());
        } catch (Exception e) {
            userRepository.delete(user);
            return new ResponseEntity<String>("User directory was unable to be created: " + e.getMessage() + " please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        

        return new ResponseEntity<String>("User registered successfully.", HttpStatus.OK);
    }

    
}
