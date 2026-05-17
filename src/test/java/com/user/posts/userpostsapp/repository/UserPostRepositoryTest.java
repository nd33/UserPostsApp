package com.user.posts.userpostsapp.repository;

import com.user.posts.userpostsapp.model.UserPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserPostRepositoryTest {

    @Autowired
    private UserPostRepository userPostRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UserPost testPost;

    @BeforeEach
    void setUp() {
        testPost = new UserPost(
                100L,
                "Test User",
                "test@example.com",
                "Test Title",
                "Test Body"
        );
        entityManager.persistAndFlush(testPost);
    }

    @Test
    public void shouldSaveAndFindById() {
        // Given
        UserPost newPost = new UserPost(
                200L,
                "New User",
                "new@example.com",
                "New Title",
                "New Body"
        );

        // When
        UserPost saved = userPostRepository.save(newPost);

        // Then
        Optional<UserPost> found = userPostRepository.findById(200L);
        assertThat(found).isPresent();
        assertThat(found.get().getUserName()).isEqualTo("New User");
    }

    @Test
    public void shouldReturnTrueForExistingPostId() {
        boolean exists = userPostRepository.existsById(100L);
        assertThat(exists).isTrue();
    }

    @Test
    public void shouldReturnFalseForNonExistingPostId() {
        boolean exists = userPostRepository.existsById(999L);
        assertThat(exists).isFalse();
    }

    @Test
    public void shouldFindAllWithPagination() {
        // Given - Add more posts
        for (int i = 1; i <= 25; i++) {
            UserPost post = new UserPost(
                    1000L + i,
                    "User " + i,
                    "user" + i + "@test.com",
                    "Title " + i,
                    "Body " + i
            );
            userPostRepository.save(post);
        }

        // When
        Pageable pageable = PageRequest.of(0, 10, Sort.by("postId").ascending());
        Page<UserPost> page = userPostRepository.findAll(pageable);

        // Then
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(26); // 1 + 25
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalPages()).isGreaterThan(0);
    }

    @Test
    public void shouldEnforceUniquePostId() {
        // Given - duplicate post_id
        UserPost duplicate = new UserPost(
                100L,  // Same as testPost's postId!
                "Duplicate User",
                "duplicate@test.com",
                "Duplicate Title",
                "Duplicate Body"
        );

        // When/Then
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            userPostRepository.saveAndFlush(duplicate);
        });
    }

    @Test
    public void shouldSetTimestampsAutomatically() {
        UserPost newPost = new UserPost(
                500L,
                "Timestamp Test",
                "timestamp@test.com",
                "Test Title",
                "Test Body"
        );

        UserPost saved = userPostRepository.save(newPost);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isEqualTo(saved.getUpdatedAt());
    }
}
