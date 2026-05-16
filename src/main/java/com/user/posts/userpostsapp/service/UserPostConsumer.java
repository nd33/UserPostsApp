package com.user.posts.userpostsapp.service;

import com.user.posts.userpostsapp.config.KafkaConfig;
import com.user.posts.userpostsapp.dto.UserPostDto;
import com.user.posts.userpostsapp.model.UserPost;
import com.user.posts.userpostsapp.repository.UserPostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer that persists UserPostDto to database.
 *
 * Now consumes UserPostDto directly because your KafkaConfig
 * uses the pure Jackson deserializer!
 */
@Service
public class UserPostConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserPostConsumer.class);

    @Autowired
    private UserPostRepository userPostRepository;

    /**
     * Consume message from Kafka topic and save to database.
     *
     * @param userPost The deserialized UserPostDto (Jackson does the work!)
     * @param acknowledgment Used to commit offset after successful processing
     */
    @KafkaListener(topics = KafkaConfig.USER_POSTS_TOPIC,
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(UserPostDto userPost, Acknowledgment acknowledgment) {
        log.debug("Received message from Kafka: postId={}", userPost.getPostId());

        try {
            // Check if post already exists (idempotency)
            if (userPostRepository.existsById(userPost.getPostId())) {
                log.warn("Post {} already exists in database, skipping", userPost.getPostId());
                acknowledgment.acknowledge();
                return;
            }

            // Convert DTO to Entity and save
            UserPost post = new UserPost(
                    userPost.getPostId(),
                    userPost.getUserName(),
                    userPost.getUserEmail(),
                    userPost.getPostTitle(),
                    userPost.getPostBody()
            );

            userPostRepository.save(post);
            log.info("Saved post {} to database", userPost.getPostId());

            // Acknowledge the message - commit offset to Kafka
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process Kafka message for postId: {}", userPost.getPostId(), e);
            // Don't acknowledge - message will be replayed
        }
    }
}