package com.johnnynguyen.honkaiwebsite.service;

import com.johnnynguyen.honkaiwebsite.model.Like;
import com.johnnynguyen.honkaiwebsite.model.Post;
import com.johnnynguyen.honkaiwebsite.model.User;
import com.johnnynguyen.honkaiwebsite.repository.LikeRepository;
import com.johnnynguyen.honkaiwebsite.repository.PostRepository;
import com.johnnynguyen.honkaiwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Like likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if user already liked the post.
        if (likeRepository.findByUserIdAndPostId(userId, postId).isPresent()) {
            throw new RuntimeException("User has already like this post");
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        return likeRepository.save(like);
    }

    public void unlikePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Like like = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new RuntimeException("No Like relationship with user and post."));
        likeRepository.delete(like);
    }

    public boolean hasUserLikedPost(Long userId, Long postId) {
        return likeRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }
}
