package com.johnnynguyen.honkaiwebsite.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Entity
@Data
@Table(name = "Users")
public class User {

    // Entity to hold social media user account.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "Email", nullable = false, unique = true, length = 320)
    private String email;

    // Ignore password in JSON output. Allows the password to be read from inputs.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "Password", nullable = false, length = 75)
    private String password;

    @Column(name = "ProfileURL", length = 500)
    private String profilePictureURL;

    @Column(length = 500)
    private String bio;

    /* Roles and getter/setter */

    // They can have more than one role.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    // Custom Methods:
    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    /* Posts attribute and getters */

    // Using Post entity, we will manage how it output on JSON.
    @JsonManagedReference("user-posts")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    // postsCount won't be in the database, but will output in JSON.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Transient
    private int postsCount;

    @JsonProperty("postsCount")
    // Accepts null inputs, so it doesn't reflect any changes during input calls from API.
    public int getPostsCount() {
        return posts != null ? posts.size() : 0;
    }

    /* Followers and Followings */

    // List of users following this user.
    @JsonManagedReference("user-followers")
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserFollowing> followers;

    // List of users this user follows.
    @JsonManagedReference("user-followings")
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserFollowing> followings;

    // Add these methods to get the counts
    @JsonProperty("followersCount")
    @Transient
    public int getFollowersCount() {
        return followers != null ? followers.size() : 0;
    }

    @JsonProperty("followingsCount")
    @Transient
    public int getFollowingCount() {
        return followings != null ? followings.size() : 0;
    }

    /* Likes */

    @JsonManagedReference("user-likes")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes;

    /* Comments */

    @JsonManagedReference("user-comments")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    /* Date created's attribute */

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Creating an account, will automatically set to the local time.
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
