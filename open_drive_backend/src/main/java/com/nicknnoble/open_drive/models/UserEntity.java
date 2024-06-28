package com.nicknnoble.open_drive.models;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Document("users")
public class UserEntity implements UserDetails {
    
    @Id
    private UUID id;
    
    private String username;

    private String password;

    private Role role;
    
    private Map<String, Directory> directories;

    private Map<String, FileEntry> files;

    public UserEntity(String username, String password, Role role) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.role = role;
        this.directories = new HashMap<String, Directory>();
        this.files = new HashMap<String, FileEntry>();
    }

    public void addDirectory(String name, String path) {

        path = path.replace('.', '|');

        Directory parentDirectory = getDirectoryByPathString(path);
        if (parentDirectory == null) {
            directories.put(name, new Directory(name));
            return;
        }
        parentDirectory.getDirectories().put(name, new Directory(name));

    }
    public void addFile(String name, String parentDir, String serverFilePath) {
        parentDir = parentDir.replace('.', '|');
        Directory parentDirectory = getDirectoryByPathString(parentDir);
        name = name.replace('.', '|');
        if (parentDirectory == null) {
            files.put(name, new FileEntry(name, serverFilePath));
            return;
        }
        parentDirectory.getFiles().put(name, new FileEntry(name, serverFilePath));
    }

    public void removeDirectory(String path) throws Exception {
        
        path = path.replace('.', '|');
        
        if (path.isEmpty()) {
            throw new Exception("Cannot remove home directory");
        }

        String[] pathParts = path.split("/");
        String directoryToRemove = pathParts[pathParts.length - 1];

        if (pathParts.length == 1) {
            directories.remove(directoryToRemove);
            return;
        }

        pathParts = Arrays.copyOf(pathParts, pathParts.length - 1);

        Map<String, Directory> currentDirectories = directories;
        Directory directory = null;

        for (String part : pathParts) {
            directory = currentDirectories.get(part);
            if (directory == null) {
                throw new RuntimeException("Directory not found");
            }
            currentDirectories = directory.getDirectories();
        }
        if(directory == null) {
            throw new Exception("Directory not found and this exception should never be thrown");
        }
        directory.getDirectories().remove(directoryToRemove);
        
    }
    
    public void removeFile(String path) throws Exception {
        
        path = path.replace('.', '|');

        String[] pathParts = path.split("/");
        String fileToRemove = pathParts[pathParts.length - 1];

        if (pathParts.length == 1) {
            files.remove(fileToRemove);
            return;
        }

        pathParts = Arrays.copyOf(pathParts, pathParts.length - 1);

        Map<String, Directory> currentDirectories = directories;
        Directory directory = null;

        for (String part : pathParts) {
            directory = currentDirectories.get(part);
            if (directory == null) {
                throw new RuntimeException("Directory not found");
            }
            currentDirectories = directory.getDirectories();
        }
        if(directory == null) {
            throw new Exception("Directory not found and this exception should never be thrown");
        }
        directory.getFiles().remove(fileToRemove);
    }

    public Directory getDirectoryByPathString(String path) throws RuntimeException {

        if (path.isEmpty()) {
            return null;
        }

        String[] pathParts = path.split("/");

        Map<String, Directory> currentDirectories = directories;
        Directory currentDirectory = null;

        for (String part : pathParts) {
            currentDirectory = currentDirectories.get(part);
            if (currentDirectory == null) {
                throw new RuntimeException("Directory not found");
            }
            currentDirectories = currentDirectory.getDirectories();
        }

        return currentDirectory;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == Role.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
