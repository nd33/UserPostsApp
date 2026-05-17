package com.user.posts.userpostsapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EmbeddedKafka(partitions = 1, topics = {KafkaConfig.USER_POSTS_TOPIC})
public class KafkaTestConfig {

    @Bean
    @Primary
    public KafkaTemplate<String, Object> testKafkaTemplate(EmbeddedKafkaBroker embeddedKafka) {
        // Use embedded Kafka for tests
        return mock(KafkaTemplate.class);
    }
}
