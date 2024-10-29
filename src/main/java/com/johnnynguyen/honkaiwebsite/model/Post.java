package com.johnnynguyen.honkaiwebsite.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "Posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Prevents infinite recursion with User.
    @JsonBackReference("user-posts")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 2200)
    private String description;

    @Column(length = 500)
    private String imageURL;

    /* Comments */

    @JsonManagedReference("post-comments")
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @JsonProperty("commentsCount")
    @Transient
    public int getCommentsCount() {
        return comments != null ? comments.size() : 0;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    /* Likes */

    @JsonManagedReference("post-likes")
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes;

    @JsonProperty("likesCount")
    @Transient
    public int getLikesCount() {
        return likes != null ? likes.size() : 0;
    }

    public void addLike(Like like) {
        likes.add(like);
    }

    public void removeLike(Like like) {
        likes.remove(like);
    }

    @Column(nullable = false)
    private LocalDateTime postAt;

    // Creating an account, will automatically set to the local time.
    @PrePersist
    protected void onCreate() {
        this.postAt = LocalDateTime.now();
    }
}
