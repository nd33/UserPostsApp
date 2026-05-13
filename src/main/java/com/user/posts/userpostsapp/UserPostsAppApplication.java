package com.user.posts.userpostsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UserPostsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserPostsAppApplication.class, args);
    }

}
