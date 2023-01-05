package com.likelionsns.final_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelionsns.final_project.domain.dto.CommentDto;
import com.likelionsns.final_project.domain.entity.Comment;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.User;
import com.likelionsns.final_project.domain.request.CommentCreateRequest;
import com.likelionsns.final_project.domain.request.CommentUpdateRequest;
import com.likelionsns.final_project.domain.response.CommentUpdateResponse;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.fixture.CommentInfoFixture;
import com.likelionsns.final_project.fixture.PostInfoFixture;
import com.likelionsns.final_project.fixture.UserInfoFixture;
import com.likelionsns.final_project.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.likelionsns.final_project.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("댓글 작성 성공")
    @WithMockUser
    void createComment() throws Exception{
        // given
        CommentCreateRequest request = new CommentCreateRequest("comment");

        given(commentService.createComment(any(), any(), any(CommentCreateRequest.class)))
                .willReturn(CommentDto.builder()
                        .id(1)
                        .comment(request.getComment())
                        .build());

        // when & then
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.comment").value(request.getComment()))
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 작성 실패(1) - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void createCommentFail01() throws Exception{
        // given
        CommentCreateRequest request = new CommentCreateRequest("comment");

        given(commentService.createComment(any(), any(), any(CommentCreateRequest.class)))
                .willReturn(CommentDto.builder()
                        .id(1)
                        .comment(request.getComment())
                        .build());

        // when & then
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 작성 성공(2) - 게시물이 존재하지 않는 경우")
    @WithMockUser
    void createCommentFail02() throws Exception{
        // given
        CommentCreateRequest request = new CommentCreateRequest("comment");

        given(commentService.createComment(any(), any(), any(CommentCreateRequest.class)))
                .willThrow(new SnsAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("POST_NOT_FOUND"))
                .andExpect(jsonPath("$.result.message").value(POST_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 수정 성공")
    @WithMockUser
    void updateComment() throws Exception{
        // given
        CommentUpdateRequest updateRequest = new CommentUpdateRequest("update comment");

        User user = UserInfoFixture.get("user1", "password1");
        Post post = PostInfoFixture.get("user1", "password1");

        Comment comment = CommentInfoFixture.get("user1", "password1");

        given(commentService.updateComment(any(), any(), any(CommentUpdateRequest.class), any()))
                .willReturn(CommentUpdateResponse.builder()
                        .id(comment.getId())
                        .comment(updateRequest.getComment())
                        .userName(comment.getUser().getUserName())
                        .postId(comment.getPost().getId())
                        .updatedAt(LocalDateTime.now())
                        .build());

        // when & then
        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.comment").value("update comment"))
                .andExpect(jsonPath("$.result.updatedAt").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 실패(1) : 인증 실패")
    @WithAnonymousUser
    void updateCommentFail01() throws Exception{
        // given
        CommentUpdateRequest updateRequest = new CommentUpdateRequest("update comment");

        given(commentService.updateComment(any(), any(), any(CommentUpdateRequest.class), any()))
                .willThrow(new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage()));

        // when & then
        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 실패(2) : 작성자 불일치")
    @WithMockUser
    void updateCommentFail02() throws Exception{
        // given
        CommentUpdateRequest updateRequest = new CommentUpdateRequest("update comment");

        given(commentService.updateComment(any(), any(), any(CommentUpdateRequest.class), any()))
                .willThrow(new SnsAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage()));

        // when & then
        mockMvc.perform(put("/api/v1/posts/1/comments/1")
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
    @DisplayName("댓글 수정 실패(3) : 데이터베이스 에러")
    @WithMockUser
    void updateCommentFail03() throws Exception{
        // given
        CommentUpdateRequest updateRequest = new CommentUpdateRequest("update comment");

        given(commentService.updateComment(any(), any(), any(CommentUpdateRequest.class), any()))
                .willThrow(new SnsAppException(DATABASE_ERROR, DATABASE_ERROR.getMessage()));

        // when & then
        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("DATABASE_ERROR"))
                .andExpect(jsonPath("$.result.message").value(DATABASE_ERROR.getMessage()))
                .andDo(print());
    }
}