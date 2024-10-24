package com.johnnynguyen.honkaiwebsite.service;

import com.johnnynguyen.honkaiwebsite.model.Post;
import com.johnnynguyen.honkaiwebsite.model.Role;
import com.johnnynguyen.honkaiwebsite.model.User;
import com.johnnynguyen.honkaiwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private FileStorageService fileStorageService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        // Assign default user role to Consumer for new users.
        user.addRole(Role.CONSUMER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User createAdminUser(User user) {
        // Add both consumer and admin to Admin users.
        user.addRole(Role.CONSUMER);
        user.addRole(Role.ADMIN);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long userId, Map<String,Object> updateUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update field only if they are not null or empty. Basically, update only necessary attributes.
        updateUser.forEach((k,v) -> {
            if (v != null) {
                switch (k) {
                    case "username" -> user.setUsername((String) v);
                    case "email" -> user.setEmail((String) v);
                    case "password" -> user.setPassword((String) v);
                    case "profilePicURL" -> user.setProfilePictureURL((String) v);
                    case "bio" -> user.setBio((String) v);
                    default -> throw new RuntimeException("Invalid key, unable to update user.");
                }
            }
        });
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensures the photo of the deleted user is deleted.
        if (user.getProfilePictureURL() != null) {
            fileStorageService.deleteFile(user.getProfilePictureURL());
        }

        // Delete all post images related the user.
        if (user.getPosts() != null) {
            user.getPosts().stream()
                    .filter(post -> post.getImageURL() != null)
                    .map(Post::getImageURL)
                    .forEach(post -> fileStorageService.deleteFile(post));
        }

        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User addRoleToUser(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.addRole(role);
        return userRepository.save(user);
    }

    public User removeRoleToUser(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.removeRole(role);
        return userRepository.save(user);
    }

    // TODO: Fix this.
    // Bulk updates if necessary.
    public User changeRoletoUser(Long userId, Set<Role> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRoles(roles);
        return userRepository.save(user);
    }

    // Allows users to change their profile picture.
    public User updateProfilePicture(Long userId, String profilePictureURL) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete old profile picture, if exists.
        String oldProfilePictureURL = user.getProfilePictureURL();
        if (oldProfilePictureURL != null && !oldProfilePictureURL.isEmpty()) {
            fileStorageService.deleteFile(oldProfilePictureURL);
        }

        user.setProfilePictureURL(profilePictureURL);
        return userRepository.save(user);
    }
}
