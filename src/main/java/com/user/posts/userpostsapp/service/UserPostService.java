package com.user.posts.userpostsapp.service;

import com.user.posts.userpostsapp.config.KafkaConfig;
import com.user.posts.userpostsapp.dto.PostDto;
import com.user.posts.userpostsapp.dto.UserDto;
import com.user.posts.userpostsapp.dto.UserPostDto;
import com.user.posts.userpostsapp.model.UserPost;
import com.user.posts.userpostsapp.repository.UserPostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Main orchestration service for user posts.
 */
@Service
public class UserPostService {

    private static final Logger log = LoggerFactory.getLogger(UserPostService.class);

    @Autowired
    private ExternalApiService externalApiService;

    @Autowired
    private DataMergerService dataMergerService;

    @Autowired
    private KafkaTemplate<String, UserPostDto> kafkaTemplate;  // Now sends UserPostDto directly!

    @Autowired
    private UserPostRepository userPostRepository;

    /**
     * Gather users and posts asynchronously, merge, and send to Kafka.
     *
     * Since KafkaTemplate now works with UserPostDto directly (thanks to your
     * pure Jackson serializer), we can send the object without manual conversion.
     */
    @Async
    public CompletableFuture<Void> gatherAndSendUserPosts() {
        log.info("Starting async gather and send process");

        // Execute both API calls in parallel using Reactor
        Mono.zip(
                externalApiService.fetchUsers(),
                externalApiService.fetchPosts()
        ).subscribe(tuple -> {
            List<UserDto> users = tuple.getT1();
            List<PostDto> posts = tuple.getT2();

            log.info("Both API calls completed - {} users, {} posts", users.size(), posts.size());

            // Merge the data
            List<UserPostDto> mergedData = dataMergerService.mergeUsersAndPosts(users, posts);

            log.info("Sending {} merged posts to Kafka", mergedData.size());

            // Send each post to Kafka - kafkaTemplate serializes UserPostDto to JSON automatically!
            for (UserPostDto userPost : mergedData) {
                try {
                    // Send with null key (will be distributed across partitions)
                    // Or use postId as key for consistent partitioning
                    kafkaTemplate.send(KafkaConfig.USER_POSTS_TOPIC,
                            String.valueOf(userPost.getPostId()),  // key (optional)
                            userPost);  // value
                    log.debug("Sent post {} to Kafka", userPost.getPostId());
                } catch (Exception e) {
                    log.error("Failed to send post {} to Kafka", userPost.getPostId(), e);
                }
            }

            log.info("Completed sending {} posts to Kafka", mergedData.size());

        }, error -> {
            log.error("Failed to fetch users/posts", error);
        });

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Get paginated user posts from database.
     */
    public Page<UserPostDto> getUserPosts(int page, int size) {
        log.info("Fetching user posts - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("postId").ascending());

        Page<UserPost> postPage = userPostRepository.findAll(pageable);

        // Convert Entity to DTO
        return postPage.map(post -> new UserPostDto(
                post.getPostId(),
                post.getUserName(),
                post.getUserEmail(),
                post.getPostTitle(),
                post.getPostBody()
        ));
    }
}