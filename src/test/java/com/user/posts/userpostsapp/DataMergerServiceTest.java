package com.user.posts.userpostsapp;


import com.user.posts.userpostsapp.dto.PostDto;
import com.user.posts.userpostsapp.dto.UserDto;
import com.user.posts.userpostsapp.dto.UserPostDto;
import com.user.posts.userpostsapp.service.DataMergerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DataMergerServiceTest {

    @InjectMocks
    private DataMergerService dataMergerService;

    @Test
    void shouldFilterPostsWithTitleLongerThan200Chars() {
        // Create a user
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        // Create posts with various title lengths
        PostDto shortTitlePost = new PostDto();
        shortTitlePost.setId(1L);
        shortTitlePost.setUserId(1L);
        shortTitlePost.setTitle("Short title");
        shortTitlePost.setBody("Body 1");

        PostDto longTitlePost = new PostDto();
        longTitlePost.setId(2L);
        longTitlePost.setUserId(1L);
        // Title exactly 201 characters
        longTitlePost.setTitle("A".repeat(201));
        longTitlePost.setBody("Body 2");

        List<UserPostDto> result = dataMergerService.mergeUsersAndPosts(
                List.of(user),
                List.of(shortTitlePost, longTitlePost)
        );

        // Only the post with title > 200 characters should be included
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPostId()).isEqualTo(2L);
        assertThat(result.get(0).getUserName()).isEqualTo("John Doe");
    }
}
