package com.user.posts.userpostsapp.dto;

/**
 * This will be used for:
 * 1. Kafka message payload
 * 2. Response format for GET /user-posts
 */
public class UserPostDto {
    private Long postId;
    private String userName;
    private String userEmail;
    private String postTitle;
    private String postBody;

    // Default constructor for Jackson/Kafka deserialization
    public UserPostDto() {
    }

    public UserPostDto(Long postId, String userName, String userEmail,
                       String postTitle, String postBody) {
        this.postId = postId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.postTitle = postTitle;
        this.postBody = postBody;
    }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getPostTitle() { return postTitle; }
    public void setPostTitle(String postTitle) { this.postTitle = postTitle; }

    public String getPostBody() { return postBody; }
    public void setPostBody(String postBody) { this.postBody = postBody; }

    @Override
    public String toString() {
        return String.format("UserPostDto{postId=%d, userName='%s', userEmail='%s', title='%s'}",
                postId, userName, userEmail,
                postTitle != null ? postTitle.substring(0, Math.min(20, postTitle.length())) : "null");
    }
}
