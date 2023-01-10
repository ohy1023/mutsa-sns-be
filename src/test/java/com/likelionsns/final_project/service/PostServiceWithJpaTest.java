package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.entity.Like;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.domain.request.UserJoinRequest;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.repository.LikeRepository;
import com.likelionsns.final_project.repository.PostRepository;
import com.likelionsns.final_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.likelionsns.final_project.exception.ErrorCode.*;
import static com.likelionsns.final_project.exception.ErrorCode.USERNAME_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PostServiceWithJpaTest {

    @Autowired
    PostService postService;
    @Autowired
    LikeService likeService;
    @Autowired
    UserService userService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        likeRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("Post 삭제 시 Like도 같이 삭제되는지 테스트")
    void deleteWithLikeDelete() {

        // given
        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("ohy")
                .password("password")
                .build();

        userService.join(userJoinRequest);

        User user = userRepository.findByUserName(userJoinRequest.getUserName())
                .orElseThrow(() -> new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("title")
                .body("body")
                .build();
        PostDto postDto = postService.createPost(postCreateRequest, user.getUserName());

        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        likeService.addCount(postDto.getId(), user.getUserName());

        Like like = likeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new SnsAppException(LIKE_NOT_FOUND, LIKE_NOT_FOUND.getMessage()));

        // when
        postService.delete(user.getUserName(), postDto.getId());

        // then
        assertThat(postRepository.findById(postDto.getId())).isEmpty();

        assertThat(likeRepository.findById(like.getId())).isEmpty();

    }
}