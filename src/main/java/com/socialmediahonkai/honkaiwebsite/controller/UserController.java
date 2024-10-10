package com.socialmediahonkai.honkaiwebsite.controller;

import com.socialmediahonkai.honkaiwebsite.model.User;
import com.socialmediahonkai.honkaiwebsite.model.Role;
import com.socialmediahonkai.honkaiwebsite.service.FileStorageService;
import com.socialmediahonkai.honkaiwebsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId)
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

    // Fix this.
    // Change role to a user.
    @PostMapping("/{userId}/roles")
    public ResponseEntity<User> changeRoleToUser(@PathVariable Long userId, @RequestBody RoleRequest roleRequest) {
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User updatedUser = userService.changeRoletoUser(userId, roleRequest.getRole());
        return ResponseEntity.ok(updatedUser);
    }

    // Static class for a return method for role as it needs Role.***.
    public static class RoleRequest {
        private Role role;

        public Role getRole() {
            return role;
        }
    }

    // Upload and set user profile picture with its URL attribute.
    // Testing API, the key is the requestParam -> profile.
    @PostMapping("/{userId}/profile-pic")
    public ResponseEntity<User> changeProfilePic(@PathVariable Long userId, @RequestParam("profile") MultipartFile profile) {
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Allowed file types
        final List<String> allowMimeTypes = List.of("image/jpeg", "image/jpg", "image/png", "image/webp");
        String fileType = profile.getContentType();
        if (!allowMimeTypes.contains(fileType) || fileType == null) {
            return ResponseEntity.badRequest().build();
        }

        String fileName = fileStorageService.storeFile(profile);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api/files/")
                .path(fileName)
                .toUriString();

        User updatedUser = userService.updateProfilePicture(userId, fileDownloadUri);
        return ResponseEntity.ok(updatedUser);
    }

}
