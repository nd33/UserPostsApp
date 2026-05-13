package com.user.posts.userpostsapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Async configuration for @Async methods.
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core pool size: threads to keep alive, even if idle
        executor.setCorePoolSize(10);

        // Max pool size: maximum threads when queue is full
        executor.setMaxPoolSize(20);

        // Queue capacity: tasks waiting for threads
        executor.setQueueCapacity(100);

        // Thread naming for debugging
        executor.setThreadNamePrefix("async-");

        // Rejection policy: when queue is full, caller runs task
        // Prevents task loss, acts as backpressure
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
