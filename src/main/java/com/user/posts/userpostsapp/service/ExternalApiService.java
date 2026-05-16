package com.user.posts.userpostsapp.service;

import com.user.posts.userpostsapp.dto.PostDto;
import com.user.posts.userpostsapp.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Service for making async HTTP calls to external APIs.
 *
 * How WebClient Works Internally:
 *
 * 1. WebClient creates an HTTP request with Reactor Netty
 * 2. Netty registers the request with its EventLoop (non-blocking)
 * 3. Mono (or Flux) is returned immediately (a promise)
 * 4. The actual HTTP response is processed when it arrives
 * 5. The calling code subscribes to the Mono to get the result
 *
 * The ParameterizedTypeReference is needed for generic types (List<T>).
 * Without it, Java type erasure would lose the List<ExternalUserDto> type.
 */
@Service
public class ExternalApiService {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiService.class);

    private final WebClient usersWebClient;
    private final WebClient postsWebClient;

    public ExternalApiService(
            @Qualifier("usersWebClient") WebClient usersWebClient,
            @Qualifier("postsWebClient") WebClient postsWebClient) {
        this.usersWebClient = usersWebClient;
        this.postsWebClient = postsWebClient;
    }

    /**
     * Fetch all users from external API asynchronously.
     *
     * Returns Mono<List<UserDto>> - a promise that will complete
     * when the HTTP response arrives.
     *
     * The .retrieve() method sends the request and returns a ResponseSpec.
     * .bodyToMono() converts the response body to a Mono.
     *
     * Error handling: onStatus captures 4xx/5xx responses.
     */
    public Mono<List<UserDto>> fetchUsers() {
        log.info("Fetching users from external API");

        return usersWebClient.get()
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            log.error("Failed to fetch users, status: {}", response.statusCode());
                            return Mono.error(new RuntimeException("Failed to fetch users: " + response.statusCode()));
                        })
                .bodyToMono(new ParameterizedTypeReference<List<UserDto>>() {})
                .doOnSuccess(users -> log.info("Successfully fetched {} users", users.size()))
                .doOnError(error -> log.error("Error fetching users: {}", error.getMessage()));
    }

    /**
     * Fetch all posts from external API asynchronously.
     *
     * Returns Mono<List<PostDto>> - a promise that will complete
     * when the HTTP response arrives.
     *
     * The .retrieve() method sends the request and returns a ResponseSpec.
     * .bodyToMono() converts the response body to a Mono.
     *
     * Error handling: onStatus captures 4xx/5xx responses.
     */
    public Mono<List<PostDto>> fetchPosts() {
        log.info("Fetching posts from external API");

        return postsWebClient.get()
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            log.error("Failed to fetch posts, status: {}", response.statusCode());
                            return Mono.error(new RuntimeException("Failed to fetch posts: " + response.statusCode()));
                        })
                .bodyToMono(new ParameterizedTypeReference<List<PostDto>>() {})
                .doOnSuccess(posts -> log.info("Successfully fetched {} posts", posts.size()))
                .doOnError(error -> log.error("Error fetching posts: {}", error.getMessage()));
    }
}
