package com.likelionsns.final_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.domain.request.PostUpdateRequest;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.fixture.PostInfoFixture;
import com.likelionsns.final_project.fixture.UserInfoFixture;
import com.likelionsns.final_project.service.LikeService;
import com.likelionsns.final_project.service.PostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.likelionsns.final_project.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @MockBean
    LikeService likeService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("포스트 작성 성공")
    @WithMockUser
    void createPost() throws Exception {
        PostCreateRequest request = PostCreateRequest.builder()
                .title("안녕하세요")
                .body("새해 복 많이 받으세요.")
                .build();

        // given
        given(postService.createPost(any(PostCreateRequest.class), any()))
                .willReturn(PostDto.builder()
                        .id(1)
                        .title(request.getTitle())
                        .body(request.getBody())
                        .createdAt(LocalDateTime.now())
                        .build());

        // when & then
        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").value(1))
                .andDo(print());
    }


    @Test
    @DisplayName("포스트 작성 실패 - 로그인 X")
    @WithMockUser
    void createPostFail() throws Exception {
        PostCreateRequest request = PostCreateRequest.builder()
                .title("안녕하세요")
                .body("새해 복 많이 받으세요.")
                .build();

        // given
        given(postService.createPost(any(PostCreateRequest.class), any()))
                .willThrow(new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                .andExpect(jsonPath("$.result.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 조회 성공")
    @WithMockUser
    void viewPost() throws Exception {

        // given
        PostDto postDto = PostDto.builder()
                .id(1)
                .title("안녕하세요")
                .body("새해 복 많이 받으세요.")
                .userName("오형상")
                .createdAt(LocalDateTime.now())
                .build();

        given(postService.findDetail(any()))
                .willReturn(postDto);

        // when 8 then
        mockMvc.perform(get("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(postDto.getId()))
                .andExpect(jsonPath("$.result.title").value(postDto.getTitle()))
                .andExpect(jsonPath("$.result.body").value(postDto.getBody()))
                .andExpect(jsonPath("$.result.userName").value(postDto.getUserName()))
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andDo(print());
    }


    @Test
    @DisplayName("포스트 수정 성공")
    @WithMockUser
    void updatePost() throws Exception {
        // given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("updated title")
                .body("updated body")
                .build();

        given(postService.update(any(), any(), any(), any()))
                .willReturn(PostDto.builder()
                        .id(1)
                        .userName("user")
                        .title(updateRequest.getTitle())
                        .body(updateRequest.getBody())
                        .build());

        // when & then
        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").value(1))
                .andDo(print());


    }

    @Test
    @DisplayName("포스트 수정 실패(1) : 인증 실패")
    @WithAnonymousUser
    void updatePostFail01() throws Exception {
        // given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("updated title")
                .body("updated body")
                .build();

        given(postService.update(any(), any(), any(), any()))
                .willThrow(new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage()));

        // when & then
        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());


    }

    @Test
    @DisplayName("포스트 수정 실패(2) : 작성자 불일치")
    @WithMockUser
    void updatePostFail02() throws Exception {
        // given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("updated title")
                .body("updated body")
                .build();

        given(postService.update(any(), any(), any(), any()))
                .willThrow(new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage()));

        // when & then
        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                .andExpect(jsonPath("$.result.message").value(INVALID_PERMISSION.getMessage()))
                .andDo(print());


    }

    @Test
    @DisplayName("포스트 수정 실패(3) : 데이터베이스 에러")
    @WithMockUser
    void updatePostFail03() throws Exception {
        // given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("updated title")
                .body("updated body")
                .build();

        given(postService.update(any(), any(), any(), any()))
                .willThrow(new SnsAppException(DATABASE_ERROR, DATABASE_ERROR.getMessage()));

        // when & then
        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("DATABASE_ERROR"))
                .andExpect(jsonPath("$.result.message").value(DATABASE_ERROR.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 삭제 성공")
    @WithMockUser
    void deletePost() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 삭제 실패(1) : 인증 실패")
    @WithAnonymousUser
    void deletePostFail01() throws Exception {

        // when & then
        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 삭제 실패(2) : 작성자 불일치")
    @WithMockUser
    void deletePostFail02() throws Exception {
        // given
        given(postService.delete(any(), any()))
                .willThrow(new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage()));

        // when & then
        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                .andExpect(jsonPath("$.result.message").value(INVALID_PERMISSION.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 삭제 실패(3) : 데이터베이스 에러")
    @WithMockUser
    void deletePostFail03() throws Exception {
        // given
        given(postService.delete(any(), any()))
                .willThrow(new SnsAppException(DATABASE_ERROR, DATABASE_ERROR.getMessage()));

        // when & then
        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("DATABASE_ERROR"))
                .andExpect(jsonPath("$.result.message").value(DATABASE_ERROR.getMessage()))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("pageable 파라미터 검증")
    void evaluates_pageable_parameter() throws Exception {

        mockMvc.perform(get("/api/v1/posts")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(postService).getAllItems(pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertThat(0).isEqualTo(pageable.getPageNumber());
        assertThat(3).isEqualTo(pageable.getPageSize());
        assertThat(Sort.by("createdAt", "desc")).isEqualTo(pageable.withSort(Sort.by("createdAt", "desc")).getSort());
    }


    @Test
    @DisplayName("마이 피드 조회 성공")
    @WithMockUser
    void myFeedSuccess() throws Exception {
        // given
        given(postService.getMyPost(any(), any()))
                .willReturn(Page.empty());

        // when & then
        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("마이 피드 조회 실패 - 로그인 X")
    @WithAnonymousUser
    void myFeedFail() throws Exception {
        // given
        given(postService.getMyPost(any(), any()))
                .willReturn(Page.empty());

        // when & then
        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}
