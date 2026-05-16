package com.user.posts.userpostsapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response for async POST /user-posts/gather.
 *
 * Returns 202 Accepted with tracking information.
 *
 * - Universally unique (no collisions across distributed systems)
 * - Can be used for logging correlation
 * - In production, you'd have GET /status/{trackingId} endpoint
 */
public class GatherResponse {
    private final String trackingId;
    private final String status;
    private final String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public GatherResponse(String status, String message) {
        this.trackingId = UUID.randomUUID().toString();
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getTrackingId() { return trackingId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
