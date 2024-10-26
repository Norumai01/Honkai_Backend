package com.johnnynguyen.honkaiwebsite.controller;

import com.johnnynguyen.honkaiwebsite.model.User;
import com.johnnynguyen.honkaiwebsite.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/user/{userId}/follow/{username}")
    public ResponseEntity<?> useFollowUser(@PathVariable Long userId, @PathVariable String username) {
        try {
            followService.followUser(userId, username);
            return ResponseEntity.ok().build();
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/user/{userId}/unfollow/{username}")
    public ResponseEntity<?> unfollowUser(@PathVariable Long userId, @PathVariable String username) {
        try {
            followService.unfollowUser(userId, username);
            return ResponseEntity.ok().build();
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/check/{username}")
    public ResponseEntity<Boolean> checkFollowStatus(@PathVariable Long userId, @PathVariable String username) {
        try {
            return ResponseEntity.ok(followService.isFollowing(userId, username));
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{username}/followers")
    public ResponseEntity<List<User>> getFollowers(@PathVariable String username) {
        try {
            return ResponseEntity.ok(followService.getFollowersByUsername(username));
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{username}/followings")
    public ResponseEntity<List<User>> getFollowings(@PathVariable String username) {
        try {
            return ResponseEntity.ok(followService.getFollowingsByUsername(username));
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
