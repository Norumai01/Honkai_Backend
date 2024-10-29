package com.johnnynguyen.honkaiwebsite.controller;

import com.johnnynguyen.honkaiwebsite.model.Comment;
import com.johnnynguyen.honkaiwebsite.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/user/{userId}/comment/{postId}")
    public ResponseEntity<?> postComment(@PathVariable Long userId, @PathVariable Long postId, @RequestBody Map<String, String> payload) {
        try {
            String commentText = payload.get("comment");
            Comment comment = commentService.createComment(userId, postId, commentText);
            return ResponseEntity.status(201).body(comment);
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{commentId}/user/{userId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {
        try {
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.noContent().build();
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getPostComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getPostComments(postId));
    }
}
