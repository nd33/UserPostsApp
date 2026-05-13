package com.user.posts.userpostsapp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response for async POST /user-posts/gather.
 *
 * Since the operation is asynchronous, we return a tracking ID immediately.
 * Client can use this for logging/correlation.
 *
 * In a real system, we will have a GET /status/{trackingId} endpoint.
 */
public class GatherResponse {
    private final String trackingId;
    private final String status;
    private final String message;
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
