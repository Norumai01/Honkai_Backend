package com.socialmediahonkai.honkaiwebsite.service;

import com.socialmediahonkai.honkaiwebsite.model.Post;
import com.socialmediahonkai.honkaiwebsite.model.User;
import com.socialmediahonkai.honkaiwebsite.repository.PostRepository;
import com.socialmediahonkai.honkaiwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Map<String,Object> updatePost) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        updatePost.forEach((k, v) -> {
            if (v != null) {
                switch (k) {
                    case "description" -> post.setDescription((String) v);
                    case "imageURL" -> post.setImageURL((String) v);
                    default -> throw new RuntimeException("Invalid key, unable to update post.");
                }
            }
        });
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    // Using the id to find user, the post will be owned by that user.
    public Post userCreatePost (Long userId, Post post) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        post.setUser(user);
        return postRepository.save(post);
    }

}
