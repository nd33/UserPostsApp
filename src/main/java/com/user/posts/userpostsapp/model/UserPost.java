package com.user.posts.userpostsapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity for storing merged user-post data.
 */
@Entity
@Table(name = "user_posts")
public class UserPost {

    @Id
    @Column(name = "post_id", nullable = false, unique = true)
    private Long postId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    @Column(name = "post_title", nullable = false, length = 500)
    private String postTitle;

    @Column(name = "post_body", nullable = false, columnDefinition = "TEXT")
    private String postBody;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected UserPost() {
    }

    public UserPost(Long postId, String userName, String userEmail,
                    String postTitle, String postBody) {
        this.postId = postId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public Long getPostId() { return postId; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getPostTitle() { return postTitle; }
    public String getPostBody() { return postBody; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("UserPost{postId=%d, userName='%s', userEmail='%s'}",
                postId, userName, userEmail);
    }
}