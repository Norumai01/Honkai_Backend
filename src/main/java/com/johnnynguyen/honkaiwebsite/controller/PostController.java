package com.johnnynguyen.honkaiwebsite.controller;

import com.johnnynguyen.honkaiwebsite.model.Post;
import com.johnnynguyen.honkaiwebsite.service.FileStorageService;
import com.johnnynguyen.honkaiwebsite.service.PostService;
import com.johnnynguyen.honkaiwebsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }

    // TODO: Polish React UI/UX experience rather than this function.
    @PostMapping("user/{userId}")
    public ResponseEntity<Post> createPost(@PathVariable Long userId, @RequestParam String title, @RequestParam String description, @RequestParam(name = "postImage", required = false) MultipartFile image) {
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        String imageURL = null;
        if (image != null && !image.isEmpty()) {
            String fileType = image.getContentType();
            if (!fileStorageService.checkIfFileValid(fileType)) {
                return ResponseEntity.badRequest().build();
            }

            String fileName = fileStorageService.storeFile(image);
            imageURL = fileStorageService.generateFileDownloadURI(fileName);
        }

        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setImageURL(imageURL);
        Post createdPost = postService.userCreatePost(userId, post, imageURL);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    // TODO: Polish React UI/UX experience rather than this function.
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestParam(required = false) String title, @RequestParam(required = false) String description, @RequestParam(required = false) MultipartFile image) {
        if (!postService.getPostById(postId).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        String imageURL = null;
        if (image != null && !image.isEmpty()) {
            String fileType = image.getContentType();
            if (!fileStorageService.checkIfFileValid(fileType)) {
                return ResponseEntity.badRequest().build();
            }

            String fileName = fileStorageService.storeFile(image);
            imageURL = fileStorageService.generateFileDownloadURI(fileName);
        }

        Post updatedPost = postService.updatePost(postId, title, description, imageURL);
        return ResponseEntity.ok(updatedPost);
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
