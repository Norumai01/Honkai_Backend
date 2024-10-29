package com.johnnynguyen.honkaiwebsite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Post_Likes")
public class Like {

    // Tracks how many likes are on the post, by users.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("user-likes")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonBackReference("post-likes")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private LocalDateTime likedAt;

    // Get the username that liked the post.
    @JsonProperty("username")
    public String getUserUsername() {
        return user != null ? user.getUsername() : null;
    }

    // Creating an account, will automatically set to the local time.
    @PrePersist
    protected void onCreate() {
        this.likedAt= LocalDateTime.now();
    }
}
