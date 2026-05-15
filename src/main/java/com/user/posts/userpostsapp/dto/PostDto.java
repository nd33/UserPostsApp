package com.user.posts.userpostsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    /**
     * Check if title length exceeds int length characters.
     * Assignment requirement: Filter OUT all posts with titles longer than 200 characters.
     */
    public boolean hasTitleShorterThan(int length) {
        return title != null && title.length() < length;
    }

    @Override
    public String toString() {
        return String.format("PostDto{id=%d, userId=%d, title='%s'}", id, userId, title);
    }
}
