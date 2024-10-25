package com.johnnynguyen.honkaiwebsite.controller;

import com.johnnynguyen.honkaiwebsite.model.Role;
import com.johnnynguyen.honkaiwebsite.model.User;
import com.johnnynguyen.honkaiwebsite.service.FileStorageService;
import com.johnnynguyen.honkaiwebsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileStorageService fileStorageService;

    // Everyone is whose authenticated can use these http methods.
    // TODO: First two methods might be debatable.

    // Might change this request to admin later.
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/createUser")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Cannot create a username that already exists.
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        // Cannot create a email that already exists.
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Upload and set user profile picture with its URL attribute.
    // Testing API, the key is the requestParam -> profile.
    // TODO: Can possibly reduce this, with added usage from FileController.
    @PostMapping("/{userId}/profile-pic")
    public ResponseEntity<User> changeProfilePic(@PathVariable Long userId, @RequestParam("profile") MultipartFile profile) {
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (profile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String fileType = profile.getContentType();
        if (!fileStorageService.checkIfFileValid(fileType)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String fileName = fileStorageService.storeFile(profile);
            String fileDownloadUri = fileStorageService.generateFileDownloadURI(fileName);

            User updatedUser = userService.updateProfilePicture(userId, fileDownloadUri);
            return ResponseEntity.ok(updatedUser);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // API Endpoints usable by Admin Role.

    @PostMapping("/createAdmin")
    public ResponseEntity<User> createAdmin(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        User createdUser = userService.createAdminUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody Map<String,Object> updates) {
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User updatedUser = userService.updateUser(userId, updates);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/roles/{role}")
    public ResponseEntity<User> addRoleToUser(@PathVariable Long userId, @PathVariable Role role) {
        User updatedUser = userService.addRoleToUser(userId, role);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}/roles/{role}")
    public ResponseEntity<User> deleteRoleFromUser(@PathVariable Long userId, @PathVariable Role role) {
        User updatedUser = userService.removeRoleToUser(userId, role);
        return ResponseEntity.ok(updatedUser);
    }

    // TODO: Fix this, cannot deserialize values.
    // Change bulk role to a user.
    @PutMapping("/{userId}/roles")
    public ResponseEntity<User> changeRoleToUser(@PathVariable Long userId, @RequestBody Set<Role> roles) {
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User updatedUser = userService.changeRoletoUser(userId, roles);
        return ResponseEntity.ok(updatedUser);
    }
}
