package com.socialmediahonkai.honkaiwebsite.controller;

import com.socialmediahonkai.honkaiwebsite.model.Post;
import com.socialmediahonkai.honkaiwebsite.service.PostService;
import com.socialmediahonkai.honkaiwebsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }

    @PostMapping("user/{userId}")
    public ResponseEntity<Post> createPost(@PathVariable Long userId, @RequestBody Post post) {
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Post createdPost = postService.userCreatePost(userId, post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Map<String,Object> updatedPost) {
        if (!postService.getPostById(postId).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Post post = postService.updatePost(postId, updatedPost);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        if (!postService.getPostById(postId).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
