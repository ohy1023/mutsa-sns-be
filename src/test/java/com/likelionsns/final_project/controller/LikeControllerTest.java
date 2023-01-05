package com.likelionsns.final_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelionsns.final_project.exception.ErrorCode;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.likelionsns.final_project.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LikeService likeService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("좋아요 누르기 성공")
    @WithMockUser
    void addLikeCount() throws Exception {

        // when & then
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").value("좋아요를 눌렀습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 로그인 X")
    @WithAnonymousUser
    void addLikeCountFail01() throws Exception{

        // when & then
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 포스트 없음")
    @WithMockUser
    void addLikeCountFail02() throws Exception{

        // given
        given(likeService.addCount(any(), any()))
                .willThrow(new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("POST_NOT_FOUND"))
                .andExpect(jsonPath("$.result.message").value(POST_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 중복 체크")
    @WithMockUser
    void addLikeCountFail03() throws Exception{

        // given
        given(likeService.addCount(any(), any()))
                .willThrow(new SnsAppException(DUPLICATED_LIKE_COUNT, DUPLICATED_LIKE_COUNT.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("DUPLICATED_LIKE_COUNT"))
                .andExpect(jsonPath("$.result.message").value(DUPLICATED_LIKE_COUNT.getMessage()))
                .andDo(print());

    }
}