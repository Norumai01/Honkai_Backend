package com.johnnynguyen.honkaiwebsite.service;

import com.johnnynguyen.honkaiwebsite.model.User;
import com.johnnynguyen.honkaiwebsite.model.UserFollowing;
import com.johnnynguyen.honkaiwebsite.repository.UserFollowingRepository;
import com.johnnynguyen.honkaiwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFollowingRepository userFollowingRepository;

    public void followUser(Long userId, String followingUsername) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User followingUser = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("User, being follow, not found."));

        // User can't follow themselves.
        if (userId.equals(followingUser.getId())) {
            throw new RuntimeException("Users cannot follow themselves.");
        }

        if (userFollowingRepository.findByFollowerIdAndFollowingId(
                userId, followingUser.getId()).isPresent()) {
            throw new RuntimeException("User is already following this user.");
        }

        UserFollowing userFollowing = new UserFollowing();
        userFollowing.setFollower(user);
        userFollowing.setFollowing(followingUser);
        userFollowingRepository.save(userFollowing);
    }

    public void unfollowUser(Long userId, String followingUsername) {

        User followingUser = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("User, being follow, not found."));

        if (userId.equals(followingUser.getId())) {
            throw new RuntimeException("Users cannot unfollow themselves.");
        }

        UserFollowing userFollowing = userFollowingRepository.findByFollowerIdAndFollowingId(userId, followingUser.getId())
                .orElseThrow(() -> new RuntimeException("Follow relationship not found."));

        userFollowingRepository.delete(userFollowing);
    }

    public boolean isFollowing(Long userId, String followingUsername) {

        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("User, being follow, not found."));

        return userFollowingRepository.findByFollowerIdAndFollowingId(
                userId, following.getId()).isPresent();
    }

    /* Method to check followers/followings for any users. */

    public List<User> getFollowersByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Gather all users, where this user is being followed.
        return userFollowingRepository.findByFollowingId(user.getId()).stream()
                .map(UserFollowing::getFollower)    // Get the users who are following. This works too, userFollowing -> userFollowing.getFollower
                .collect(Collectors.toList());
    }

    public List<User> getFollowingsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Gather all users, that this user is following.
        return userFollowingRepository.findByFollowerId(user.getId()).stream()
                .map(UserFollowing::getFollowing)   // Get the users, they're following.
                .collect(Collectors.toList());
    }
}
