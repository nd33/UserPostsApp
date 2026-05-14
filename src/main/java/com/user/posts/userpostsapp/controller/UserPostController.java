package com.user.posts.userpostsapp.controller;

import com.user.posts.userpostsapp.dto.GatherResponse;
import com.user.posts.userpostsapp.dto.PageResponse;
import com.user.posts.userpostsapp.dto.UserPostDto;
import com.user.posts.userpostsapp.service.UserPostService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user posts endpoints.
 */
@RestController
@RequestMapping("/user-posts")
@Validated
public class UserPostController {

    private static final Logger log = LoggerFactory.getLogger(UserPostController.class);

    @Autowired
    private UserPostService userPostService;

    /**
     * POST /user-posts/gather
     *
     * Trigger asynchronous gathering of users and posts from external APIs.
     *
     * Why async? The external API calls could take 1-2 seconds.
     * Instead of blocking the HTTP thread, we return immediately
     * with a tracking ID, and processing continues in background.
     *
     * HTTP Response: 202 Accepted (not 200 OK) - standard for async operations
     *
     * @return GatherResponse with tracking ID and status
     */
    @PostMapping("/gather")
    public ResponseEntity<GatherResponse> gatherUserPosts() {
        log.info("Received POST request to gather user posts");

        // Trigger async processing
        userPostService.gatherAndSendUserPosts();

        // Return immediately with 202 Accepted
        // The client knows the request was received and is being processed
        GatherResponse response = new GatherResponse(
                "ACCEPTED",
                "User posts gathering started asynchronously"
        );

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }

    /**
     * GET /user-posts?page={page}&size={size}
     *
     * Get paginated user posts from database.
     *
     * Request parameters validation:
     * - @RequestParam(defaultValue = "0") - If not provided, defaults to 0
     * - @Min(0) - Page number cannot be negative
     * - @Positive - Ensures positive number
     * - @Max(100) - Limit page size to prevent abuse (100 items max)
     *
     * Spring automatically:
     * 1. Extracts 'page' and 'size' from query string
     * 2. Validates them with @Min/@Max annotations
     * 3. Converts to int (automatic type conversion)
     * 4. If validation fails, throws ConstraintViolationException (handled by global handler)
     *
     * @param page Page number (0-indexed, default 0)
     * @param size Items per page (default 20, max 100)
     * @return Paginated response with content and metadata
     */
    @GetMapping
    public ResponseEntity<PageResponse<UserPostDto>> getUserPosts(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        log.info("Received GET request for user posts - page: {}, size: {}", page, size);

        // Fetch paginated data from service
        Page<UserPostDto> postPage = userPostService.getUserPosts(page, size);

        // Wrap in custom page response
        PageResponse<UserPostDto> response = new PageResponse<>(postPage);

        return ResponseEntity.ok(response);
    }
}
