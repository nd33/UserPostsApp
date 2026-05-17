package com.user.posts.userpostsapp.service;

import com.user.posts.userpostsapp.dto.PostDto;
import com.user.posts.userpostsapp.dto.UserDto;
import com.user.posts.userpostsapp.dto.UserPostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DataMergerServiceTest {

    private DataMergerService dataMergerService;

    @BeforeEach
    void setUp() {
        dataMergerService = new DataMergerService();
    }


        @Test
        void shouldMergeUsersAndPosts() {
            // Given
            UserDto user = new UserDto();
            user.setId(1L);
            user.setName("John Doe");
            user.setEmail("john@example.com");

            PostDto post = new PostDto();
            post.setId(100L);
            post.setUserId(1L);
            post.setTitle("Test Post Title");
            post.setBody("Test post body content");

            // When
            List<UserPostDto> result = dataMergerService.mergeUsersAndPosts(
                    List.of(user),
                    List.of(post)
            );

            // Then
            assertThat(result).hasSize(1);
            UserPostDto merged = result.get(0);
            assertThat(merged.getPostId()).isEqualTo(100L);
            assertThat(merged.getUserName()).isEqualTo("John Doe");
            assertThat(merged.getUserEmail()).isEqualTo("john@example.com");
            assertThat(merged.getPostTitle()).isEqualTo("Test Post Title");
            assertThat(merged.getPostBody()).isEqualTo("Test post body content");
        }

        @Test
        void shouldMergeMultiplePostsWithCorrectUserMapping() {
            // Given
            UserDto user1 = createUser(1L, "User One", "user1@test.com");
            UserDto user2 = createUser(2L, "User Two", "user2@test.com");

            PostDto post1 = createPost(101L, 1L, "Post 1");
            PostDto post2 = createPost(102L, 1L, "Post 2");
            PostDto post3 = createPost(103L, 2L, "Post 3");

            // When
            List<UserPostDto> result = dataMergerService.mergeUsersAndPosts(
                    List.of(user1, user2),
                    List.of(post1, post2, post3)
            );

            // Then
            assertThat(result).hasSize(3);

            // Check user1's posts
            assertThat(result.get(0).getUserName()).isEqualTo("User One");
            assertThat(result.get(1).getUserName()).isEqualTo("User One");

            // Check user2's post
            assertThat(result.get(2).getUserName()).isEqualTo("User Two");
        }

        @Test
        void shouldNotKeepPostsWithLongTitle() {
            // Given
            UserDto user = createUser(1L, "Test User", "test@test.com");

            PostDto shortPost = createPost(1L, 1L, "Short title");
            PostDto longPost = createPost(2L, 1L, "A".repeat(201)); // 201 chars

            // When
            List<UserPostDto> result = dataMergerService.mergeUsersAndPosts(
                    List.of(user),
                    List.of(shortPost, longPost)
            );

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPostId()).isEqualTo(1L);
        }

        @Test
        void shouldNotFilterOutPostsWithShortTitle() {
            // Given
            UserDto user = createUser(1L, "Test User", "test@test.com");
            PostDto post = createPost(1L, 1L, "Short title (less than 200 chars)");

            // When
            List<UserPostDto> result = dataMergerService.mergeUsersAndPosts(
                    List.of(user),
                    List.of(post)
            );

            // Then
            assertThat(result).size().isEqualTo(1);
        }

        @Test
        void shouldFilterOutPostWithExactly200Chars() {
            // Given
            UserDto user = createUser(1L, "Test User", "test@test.com");
            PostDto post = createPost(1L, 1L, "A".repeat(200));

            // When
            List<UserPostDto> result = dataMergerService.mergeUsersAndPosts(
                    List.of(user),
                    List.of(post)
            );

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        void shouldHandleEmptyUsers() {
            // Given
            PostDto post = createPost(1L, 1L, "Some title");

            // When
            List<UserPostDto> result = dataMergerService.mergeUsersAndPosts(
                    Collections.emptyList(),
                    List.of(post)
            );

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        void shouldHandleEmptyPosts() {
            // Given
            UserDto user = createUser(1L, "Test User", "test@test.com");

            // When
            List<UserPostDto> result = dataMergerService.mergeUsersAndPosts(
                    List.of(user),
                    Collections.emptyList()
            );

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        void shouldHandleNullUser() {
            // Given
            UserDto user = createUser(1L, "Test User", "test@test.com");
            PostDto postWithMissingUser = createPost(1L, 999L, "Post with missing user");

            // When
            List<UserPostDto> result = dataMergerService.mergeUsersAndPosts(
                    List.of(user),
                    List.of(postWithMissingUser)
            );

            // Then - post with missing user should be filtered out
            assertThat(result).isEmpty();
        }

        @Test
        void shouldHandleDuplicateUserIds() {
            // Given
            UserDto user1 = createUser(1L, "First User", "first@test.com");
            UserDto user2 = createUser(1L, "Second User", "second@test.com"); // Same ID!
            PostDto post = createPost(1L, 1L, "Test post");

            // When
            List<UserPostDto> result = dataMergerService.mergeUsersAndPosts(
                    List.of(user1, user2),
                    List.of(post)
            );

            // Then - should use the first user (user1)
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUserName()).isEqualTo("First User");
        }

    // Helper methods
    private UserDto createUser(Long id, String name, String email) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private PostDto createPost(Long id, Long userId, String title) {
        PostDto post = new PostDto();
        post.setId(id);
        post.setUserId(userId);
        post.setTitle(title);
        post.setBody("Test body for post " + id);
        return post;
    }
}