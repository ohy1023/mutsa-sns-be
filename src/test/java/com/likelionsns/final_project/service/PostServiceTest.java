package com.likelionsns.final_project.service;

import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.request.CommentUpdateRequest;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.domain.request.PostUpdateRequest;
import com.likelionsns.final_project.domain.response.CommentUpdateResponse;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.fixture.PostInfoFixture;
import com.likelionsns.final_project.fixture.UserInfoFixture;
import com.likelionsns.final_project.repository.CommentRepository;
import com.likelionsns.final_project.repository.LikeRepository;
import com.likelionsns.final_project.repository.PostRepository;
import com.likelionsns.final_project.repository.UserRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.likelionsns.final_project.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doNothing;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    User user = UserInfoFixture.get("user1", "password1");
    User user2 = UserInfoFixture.get("user2", "password2");
    Post post = PostInfoFixture.get(user.getUserName(), user.getPassword());

    @Test
    @DisplayName("포스트 등록 성공")
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
        System.out.println(postDto.toString());

    }

    @Test
    @DisplayName("포스트 등록 실패 - 로그인 X")
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

    @Test
    @DisplayName("포스트 조회 성공")
    void viewPost() {

        // given
        given(postRepository.findById(any()))
                .willReturn(Optional.of(post));

        // when
        PostDto postDto = postService.findDetail(post.getId());

        // then
        assertThat(postDto.getUserName()).isEqualTo(post.getUser().getUserName());

    }

    @Test
    @DisplayName("포스트 조회 실패 - 포스트 없을 경우")
    void viewPostFail() {

        // given
        given(postRepository.findById(any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.findDetail(post.getId()))
                .isExactlyInstanceOf(SnsAppException.class)
                .hasMessage(POST_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("포스트 수정 성공")
    void updatePost() {
        // given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("updated title")
                .body("updated body")
                .build();

        given(userRepository.findByUserName(user.getUserName()))
                .willReturn(Optional.of(user));

        given(postRepository.findById(post.getId()))
                .willReturn(Optional.of(post));

        given(postRepository.save(post))
                .willReturn(post);


        // when
        PostDto postDto = postService.update(post.getId(), user.getUserName(), updateRequest.getTitle(), updateRequest.getBody());

        // then
        assertThat(postDto.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(postDto.getBody()).isEqualTo(updateRequest.getBody());
    }

    @Test
    @DisplayName("포스트 수정 실패(1) : 포스트 존재하지 않음")
    void updatePostFail01() {
        // given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("updated title")
                .body("updated body")
                .build();

        given(postRepository.findById(post.getId()))
                .willReturn(Optional.empty());

        given(userRepository.findByUserName(any()))
                .willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> postService.update(post.getId(), post.getUser().getUserName(), updateRequest.getTitle(), updateRequest.getBody()))
                .isExactlyInstanceOf(SnsAppException.class)
                .hasMessage(POST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("포스트 수정 실패 : 작성자!= 유저 ")
    void updatePostFail02() {
        // given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("updated title")
                .body("updated body")
                .build();

        given(postRepository.findById(post.getId()))
                .willReturn(Optional.of(post));

        given(userRepository.findByUserName(any()))
                .willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> postService.update(post.getId(), user2.getUserName(), updateRequest.getTitle(), updateRequest.getBody()))
                .isExactlyInstanceOf(SnsAppException.class)
                .hasMessage(INVALID_PERMISSION.getMessage());
    }

    @Test
    @DisplayName("포스트 수정 실패 : 유저 존재하지 않음")
    void updatePostFail03() {
        // given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("updated title")
                .body("updated body")
                .build();

        given(userRepository.findByUserName(any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.update(post.getId(), post.getUser().getUserName(), updateRequest.getTitle(), updateRequest.getBody()))
                .isExactlyInstanceOf(SnsAppException.class)
                .hasMessage(USERNAME_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("포스트 삭제 성공")
    void deletePost() {

        given(userRepository.findByUserName(user.getUserName()))
                .willReturn(Optional.of(user));

        given(postRepository.findById(post.getId()))
                .willReturn(Optional.of(post));

//        willDoNothing().given(commentRepository).deleteAllByPost(post);
//        willDoNothing().given(likeRepository).deleteAllByPost(post);
//        willDoNothing().given(postRepository).delete(post);

        boolean delete = postService.delete(user.getUserName(), post.getId());

        assertThat(delete).isTrue();
    }

    @Test
    @DisplayName("포스트 삭제 실패 : 유저 존재하지 않음")
    void deletePostFail01() {
        // given
        given(postRepository.findById(post.getId()))
                .willReturn(Optional.of(post));

        given(userRepository.findByUserName(user.getUserName()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.delete(user.getUserName(), post.getId()))
                .isExactlyInstanceOf(SnsAppException.class)
                .hasMessage(USERNAME_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("포스트 삭제 실패 : 포스트 존재하지 않음")
    void deletePostFail02() {
        // given
        given(postRepository.findById(post.getId()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.delete(user.getUserName(), post.getId()))
                .isExactlyInstanceOf(SnsAppException.class)
                .hasMessage(POST_NOT_FOUND.getMessage());

    }
}