package com.johnnynguyen.honkaiwebsite.repository;

import com.johnnynguyen.honkaiwebsite.model.UserFollowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface UserFollowingRepository extends JpaRepository<UserFollowing, Long> {

    // Find if a relationship exists.
    Optional<UserFollowing> findUserFollowerIdAndUserFollowingId(Long FollowerId, Long FollowingId);

    // Get all users where this user is being followed by (the followers).
    List<UserFollowing> findByFollowingId(Long FollowingId);

    // Get all users that this user is following (the following).
    List<UserFollowing> findByFollowerId(Long FollowerId);

    // Delete a relationship.
    void deleteByUserFollowerIdAndUserFollowingId(Long FollowerId, Long FollowingId);
}
