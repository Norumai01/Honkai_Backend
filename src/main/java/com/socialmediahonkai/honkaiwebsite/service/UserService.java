package com.socialmediahonkai.honkaiwebsite.service;

import com.socialmediahonkai.honkaiwebsite.model.Role;
import com.socialmediahonkai.honkaiwebsite.model.User;
import com.socialmediahonkai.honkaiwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
        user.setRole(Role.CONSUMER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User createAdminUser(User user) {
        // Add both consumer and admin to Admin users.
        user.setRole(Role.ADMIN);
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
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User changeRoletoUser(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(role);
        return userRepository.save(user);
    }

    public User updateProfilePicture(Long userId, String profilePictureURL) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfilePictureURL(profilePictureURL);
        return userRepository.save(user);
    }
}
