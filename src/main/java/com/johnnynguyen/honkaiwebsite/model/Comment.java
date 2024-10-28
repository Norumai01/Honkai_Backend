package com.johnnynguyen.honkaiwebsite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Post_Comments")
public class Comment {

    // Make comment on post, by users.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Avoid recursion of selected entities.
    @JsonBackReference("user-comments")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonBackReference("post-comments")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime commentAt;

    // Creating an account, will automatically set to the local time.
    @PrePersist
    protected void onCreate() {
        this.commentAt= LocalDateTime.now();
    }
}
