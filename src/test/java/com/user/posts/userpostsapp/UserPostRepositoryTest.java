//package com.user.posts.userpostsapp;
//
//import com.user.posts.userpostsapp.model.UserPost;
//import com.user.posts.userpostsapp.repository.UserPostRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//public class UserPostRepositoryTest {
//
//    @Autowired
//    private UserPostRepository repository;
//
//    @Test
//    void shouldSaveAndFindByPostId() {
//        UserPost post = new UserPost(101L, "John Doe", "john@test.com",
//                "Title", "Body");
//        UserPost saved = repository.save(post);
//
//        assertThat(saved.getId()).isNotNull();
//        assertThat(repository.findByPostId(101L)).isPresent();
//    }
//
//    @Test
//    void shouldPaginateResults() {
//        for (int i = 0; i < 25; i++) {
//            repository.save(new UserPost((long) i, "User" + i,
//                    "user@test.com", "Title", "Body"));
//        }
//
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<UserPost> page = repository.findAll(pageable);
//
//        assertThat(page.getTotalElements()).isEqualTo(25);
//        assertThat(page.getTotalPages()).isEqualTo(3);
//        assertThat(page.getContent()).hasSize(10);
//    }
//}
//
