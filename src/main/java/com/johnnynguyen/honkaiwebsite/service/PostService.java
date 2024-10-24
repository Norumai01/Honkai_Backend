package com.johnnynguyen.honkaiwebsite.service;

import com.johnnynguyen.honkaiwebsite.model.User;
import com.johnnynguyen.honkaiwebsite.repository.PostRepository;
import com.johnnynguyen.honkaiwebsite.model.Post;
import com.johnnynguyen.honkaiwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    // Could be thrown away.
//    public Post createPost(Post post) {
//        return postRepository.save(post);
//    }

    public Post updatePost(Long id, String title, String description, String imageURL) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (title != null && !title.isEmpty()) {
            post.setTitle(title);
        }
        if (description != null && !description.isEmpty()) {
            post.setDescription(description);
        }
        if (imageURL != null && !imageURL.isEmpty()) {
            String oldPostImageURL = post.getImageURL();

            post.setImageURL(imageURL);
            fileStorageService.deleteFile(oldPostImageURL);
        }

        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Ensures any photos from deleted post is deleted.
        if (post.getImageURL() != null) {
            fileStorageService.deleteFile(post.getImageURL());
        }

        postRepository.deleteById(id);
    }

    // Using the id to find user, the post will be owned by that user.
    public Post userCreatePost (Long userId, Post post, String imageURL) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        post.setUser(user);
        post.setImageURL(imageURL);
        return postRepository.save(post);
    }

    // TODO: May consider deleting this.
    public void addImageToPost(Long postId, String imageURL) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setImageURL(imageURL);
        postRepository.save(post);
    }

}
