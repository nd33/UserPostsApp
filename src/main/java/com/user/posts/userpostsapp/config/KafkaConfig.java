package com.user.posts.userpostsapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.posts.userpostsapp.dto.UserPostDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public static final String USER_POSTS_TOPIC = "user-posts";

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public NewTopic userPostsTopic() {
        return TopicBuilder.name(USER_POSTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * Converts UserPostDto → JSON bytes.
     */
    private Serializer<UserPostDto> userPostSerializer() {
        return (topic, data) -> {
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(data);
                log.debug("Serialized UserPostDto {} to {} bytes", data.getPostId(), bytes.length);
                return bytes;
            } catch (Exception e) {
                log.error("Failed to serialize UserPostDto: {}", data, e);
                throw new RuntimeException("Failed to serialize UserPostDto", e);
            }
        };
    }

    /**
     * Converts JSON bytes → UserPostDto.
     */
    private Deserializer<UserPostDto> userPostDeserializer() {
        return (topic, data) -> {
            try {
                UserPostDto dto = objectMapper.readValue(data, UserPostDto.class);
                log.debug("Deserialized {} bytes to UserPostDto postId={}", data.length, dto.getPostId());
                return dto;
            } catch (Exception e) {
                log.error("Failed to deserialize {} bytes", data.length, e);
                throw new RuntimeException("Failed to deserialize UserPostDto", e);
            }
        };
    }


    @Bean
    public ProducerFactory<String, UserPostDto> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        config.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        return new DefaultKafkaProducerFactory<>(
                config,
                new StringSerializer(),
                userPostSerializer()
        );
    }

    @Bean
    public KafkaTemplate<String, UserPostDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, UserPostDto> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "user-posts-consumer-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                userPostDeserializer()
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserPostDto>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserPostDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}