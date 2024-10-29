package com.johnnynguyen.honkaiwebsite.service;

import com.johnnynguyen.honkaiwebsite.model.Comment;
import com.johnnynguyen.honkaiwebsite.model.Post;
import com.johnnynguyen.honkaiwebsite.model.User;
import com.johnnynguyen.honkaiwebsite.repository.CommentRepository;
import com.johnnynguyen.honkaiwebsite.repository.PostRepository;
import com.johnnynguyen.honkaiwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Comment createComment(Long userId, Long postId, String commentText) {

        // Check for empty string.
        if (commentText == null || commentText.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be null or empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));


        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setComment(commentText.trim());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(userId) && !comment.getPost().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete other user comment.");
        }

        commentRepository.delete(comment);
    }

    public List<Comment> getPostComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return commentRepository.findByPostId(postId);
    }
}
