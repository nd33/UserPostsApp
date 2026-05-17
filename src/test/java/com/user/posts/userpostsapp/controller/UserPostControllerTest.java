package com.user.posts.userpostsapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.posts.userpostsapp.model.UserPost;
import com.user.posts.userpostsapp.repository.UserPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserPostRepository userPostRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // Add test data
        for (int i = 1; i <= 50; i++) {
            UserPost post = new UserPost(
                    (long) i,
                    "Test User " + i,
                    "user" + i + "@test.com",
                    "Test Title " + i,
                    "Test Body " + i
            );
            userPostRepository.save(post);
        }
    }

    @Test
    public void gatherUserPostsShouldReturnAccepted() throws Exception {
        mockMvc.perform(post("/user-posts/gather"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.trackingId").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void getUserPostsShouldReturnPaginatedResults() throws Exception {
        MvcResult result = mockMvc.perform(get("/user-posts")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(50))
                .andExpect(jsonPath("$.totalPages").value(5))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false))
                .andReturn();
    }

    @Test
    public void getUserPostsShouldUseDefaults() throws Exception {
        mockMvc.perform(get("/user-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.pageNumber").value(0));
    }

    @Test
    public void getUserPostsShouldHandleSecondPage() throws Exception {
        mockMvc.perform(get("/user-posts")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.first").value(false));
    }

    @Test
    public void getUserPostsShouldRejectNegativePage() throws Exception {
        mockMvc.perform(get("/user-posts")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    public void getUserPostsShouldRejectExcessivePageSize() throws Exception {
        mockMvc.perform(get("/user-posts")
                        .param("page", "0")
                        .param("size", "200"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    public void getUserPostsShouldRejectInvalidPageSize() throws Exception {
        mockMvc.perform(get("/user-posts")
                        .param("page", "0")
                        .param("size", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserPostsShouldSortAscending() throws Exception {
        MvcResult result = mockMvc.perform(get("/user-posts")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        // Verify first post has ID 1
        assertThat(content).contains("\"postId\":1");
    }
}
