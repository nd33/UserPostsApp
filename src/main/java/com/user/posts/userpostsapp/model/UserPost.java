package com.user.posts.userpostsapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Simplified JPA Entity for storing merged user-post data.
 */
@Entity
@Table(name = "user_posts",
        indexes = {
                @Index(name = "idx_post_id", columnList = "post_id"),  // For faster lookups
                @Index(name = "idx_user_email", columnList = "user_email")  // For potential future queries
        },
        uniqueConstraints = @UniqueConstraint(columnNames = "post_id"))
public class UserPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getPostTitle() { return postTitle; }
    public String getPostBody() { return postBody; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    protected void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("UserPost{postId=%d, userName='%s', userEmail='%s', title='%s'}",
                postId, userName, userEmail, postTitle.substring(0, Math.min(20, postTitle.length())));
    }
}