package com.nicknnoble.open_drive.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;

import com.nicknnoble.open_drive.models.UserEntity;



public interface UserRepository extends CassandraRepository<UserEntity, UUID> {
    
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
}
