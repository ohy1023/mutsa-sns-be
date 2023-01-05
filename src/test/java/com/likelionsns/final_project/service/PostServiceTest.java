package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.fixture.PostInfoFixture;
import com.likelionsns.final_project.fixture.UserInfoFixture;
import com.likelionsns.final_project.repository.PostRepository;
import com.likelionsns.final_project.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.likelionsns.final_project.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    User user = UserInfoFixture.get("user1", "password1");
    Post post = PostInfoFixture.get(user.getUserName(),user.getPassword());

    @Test
    @DisplayName("등록 성공")
    void createPost() {

        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("title")
                .body("body")
                .build();

        given(userRepository.findByUserName(user.getUserName()))
                .willReturn(Optional.of(user));

        given(postRepository.save(any()))
                .willReturn(post);

        // when
        PostDto postDto = postService.createPost(request, user.getUserName());

        // then
        assertThat(postDto.getId()).isEqualTo(post.getId());

    }

    @Test
    @DisplayName("등록 실패 - 로그인 X")
    void createPostFail() {

        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("title")
                .body("body")
                .build();

        given(userRepository.findByUserName(user.getUserName()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.createPost(request, user.getUserName()))
                .isExactlyInstanceOf(SnsAppException.class)
                .hasMessage(USERNAME_NOT_FOUND.getMessage());

    }

}