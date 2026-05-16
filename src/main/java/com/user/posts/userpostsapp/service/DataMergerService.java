package com.user.posts.userpostsapp.service;


import com.user.posts.userpostsapp.dto.PostDto;
import com.user.posts.userpostsapp.dto.UserDto;
import com.user.posts.userpostsapp.dto.UserPostDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for merging users and posts data.
 *
 * Assignment requirements:
 * 1. Filter OUT posts with title longer than 200 characters
 * 2. Merge user information into posts (userName, userEmail)
 * 3. Each post should contain user info within it
 */
@Service
public class DataMergerService {

    private static final Logger log = LoggerFactory.getLogger(DataMergerService.class);

    /**
     * Merge users and posts into UserPostDto objects.
     *
     * Algorithm:
     * 1. Convert users list to Map<userId, ExternalUserDto> for O(1) lookup
     * 2. Filter posts by title length (keep if < 200 chars)
     * 3. For each filtered post, find user from map and merge
     * 4. Create UserPostDto with required fields
     *
     * Time complexity: O(n + m) where n=users, m=posts
     * Space complexity: O(n + filteredPosts)
     *
     * @param users List of users from external API
     * @param posts List of posts from external API
     * @return List of merged UserPostDto objects
     */
    public List<UserPostDto> mergeUsersAndPosts(List<UserDto> users, List<PostDto> posts) {
        log.info("Merging {} users and {} posts", users.size(), posts.size());

        // Create user map for O(1) lookups
        // This is more efficient than iterating through users for each post
        Map<Long, UserDto> userMap = users.stream()
                .collect(Collectors.toMap(
                        UserDto::getId,
                        user -> user,
                        (existing, replacement) -> existing  // In case of duplicate IDs
                ));

        log.debug("Created user map with {} entries", userMap.size());

        // Filter OUT posts with title > 200 characters and merge with user data
        List<UserPostDto> mergedPosts = posts.stream()
                .filter(post -> {
                    boolean keep = post.hasTitleShorterThan(200);
                    if (keep) {
                        log.debug("Post {} title length {} < 200, keeping",
                                post.getId(), post.getTitle().length());
                    }
                    return keep;
                })
                .map(post -> {
                    UserDto user = userMap.get(post.getUserId());

                    if (user == null) {
                        log.warn("No user found for post {} with userId {}", post.getId(), post.getUserId());
                        return null;
                    }

                    return new UserPostDto(post.getId(), user.getName(), user.getEmail(), post.getTitle(), post.getBody());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Merged {} posts (filtered from {})", mergedPosts.size(), posts.size());
        return mergedPosts;
    }
}