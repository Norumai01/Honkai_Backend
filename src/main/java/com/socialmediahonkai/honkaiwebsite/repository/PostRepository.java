package com.socialmediahonkai.honkaiwebsite.repository;

import com.socialmediahonkai.honkaiwebsite.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserId(Long userId);

    List<Post> findByUsername(String username);
}
