package com.johnnynguyen.honkaiwebsite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "User_Followers")
public class UserFollowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ignoring any JSON output related to User, avoiding reoccurrence.
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @Column(nullable = false)
    private LocalDateTime followedAt;

    @PrePersist
    protected void onCreate() {
        this.followedAt = LocalDateTime.now();
    }

    // Custom Getters
    @JsonProperty("followerUsername")
    public String getFollowerUsername() {
        return follower != null ? follower.getUsername() : null;
    }

    @JsonProperty("followingUsername")
    public String getFollowingUsername() {
        return following != null ? following.getUsername() : null;
    }

}
