package com.user.posts.userpostsapp.repository;

import com.user.posts.userpostsapp.model.UserPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPostRepository extends JpaRepository<UserPost, Long> {

    Optional<UserPost> findById(Long postId);

    boolean existsById(Long postId);

    Page<UserPost> findAll(Pageable pageable);
}
