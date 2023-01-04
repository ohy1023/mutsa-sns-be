package com.likelionsns.final_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.likelionsns.final_project.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

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
}