package com.user.posts.userpostsapp.repository;

import com.user.posts.userpostsapp.model.UserPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserPostRepository extends JpaRepository<UserPost, Long> {

    Optional<UserPost> findByPostId(Long postId);


    boolean existsByPostId(Long postId);

    Page<UserPost> findAll(Pageable pageable);

    @Query("SELECT u FROM UserPost u WHERE u.userEmail = :email")
    Page<UserPost> findByUserEmail(@Param("email") String email, Pageable pageable);

    @Modifying
    @Query("DELETE FROM UserPost u WHERE u.createdAt < :date")
    int deleteOlderThan(@Param("date") LocalDateTime date);
}
