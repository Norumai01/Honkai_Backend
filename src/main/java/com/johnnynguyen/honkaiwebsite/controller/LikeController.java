package com.johnnynguyen.honkaiwebsite.controller;

import com.johnnynguyen.honkaiwebsite.model.Like;
import com.johnnynguyen.honkaiwebsite.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/user/{userId}/like/{postId}")
    public ResponseEntity<?> likePost(@PathVariable Long userId, @PathVariable Long postId) {
        try {
            Like like = likeService.likePost(userId, postId);
            return ResponseEntity.ok(like);
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/user/{userId}/unlike/{postId}")
    public ResponseEntity<?> unlikePost(@PathVariable Long userId, @PathVariable Long postId) {
        try {
            likeService.unlikePost(userId, postId);
            return ResponseEntity.noContent().build();
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/like/{postId}")
    public ResponseEntity<Boolean> checkLikeStatus(@PathVariable Long userId, @PathVariable Long postId) {
        return ResponseEntity.ok(likeService.hasUserLikedPost(userId, postId));
    }
}
