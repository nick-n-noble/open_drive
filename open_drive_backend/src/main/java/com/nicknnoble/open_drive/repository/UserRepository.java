package com.nicknnoble.open_drive.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nicknnoble.open_drive.models.UserEntity;



public interface UserRepository extends MongoRepository<UserEntity, UUID> {
    
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
}
