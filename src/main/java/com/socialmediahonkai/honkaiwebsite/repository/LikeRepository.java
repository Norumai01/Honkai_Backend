package com.socialmediahonkai.honkaiwebsite.repository;

import com.socialmediahonkai.honkaiwebsite.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findByUserId(Long userId);

    List<Like> findByPostId(Long postId);

    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
}
