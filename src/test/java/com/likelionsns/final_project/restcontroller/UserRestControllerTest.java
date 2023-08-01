package com.likelionsns.final_project.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelionsns.final_project.controller.api.UserRestController;
import com.likelionsns.final_project.domain.request.UserJoinRequest;
import com.likelionsns.final_project.domain.request.UserLoginRequest;
import com.likelionsns.final_project.domain.response.UserJoinResponse;
import com.likelionsns.final_project.exception.SnsAppException;
import com.likelionsns.final_project.service.AlarmService;
import com.likelionsns.final_project.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    AlarmService alarmService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser
    void join() throws Exception {
        // given
        UserJoinRequest request = UserJoinRequest.builder()
                .userName("오형상")
                .password("ohy1023")
                .build();
        given(userService.join(any(UserJoinRequest.class)))
                .willReturn(new UserJoinResponse(1, request.getUserName()));

        //when & then
        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userId").value(1))
                .andExpect(jsonPath("$.result.userName").value("오형상"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - userName 중복")
    @WithMockUser
    void joinError() throws Exception {
        // given
        UserJoinRequest request = UserJoinRequest.builder()
                .userName("오형상")
                .password("ohy1023")
                .build();
        given(userService.join(any(UserJoinRequest.class)))
                .willThrow(new SnsAppException(DUPLICATED_USER_NAME, DUPLICATED_USER_NAME.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("DUPLICATED_USER_NAME"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void login() throws Exception {
        // given
        UserLoginRequest request = UserLoginRequest.builder()
                .userName("오형상")
                .password("ohy1023")
                .build();
        given(userService.login(any(), any())).willReturn("token");
        // when & then
        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.jwt").value("token"))
                .andDo(print());

    }

    @Test
    @DisplayName("로그인 실패 - userName 존재 X")
    @WithMockUser
    void loginFail01() throws Exception{
        // given
        UserLoginRequest request = UserLoginRequest.builder()
                .userName("오형상")
                .password("ohy1023")
                .build();
        given(userService.login(any(), any()))
                .willThrow(new SnsAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
                .andDo(print());

    }

    @Test
    @DisplayName("로그인 실패 - password 불일치")
    @WithMockUser
    void loginFail02() throws Exception {
        // given
        UserLoginRequest request = UserLoginRequest.builder()
                .userName("오형상")
                .password("ohy1023")
                .build();
        given(userService.login(any(), any()))
                .willThrow(new SnsAppException(INVALID_PASSWORD, INVALID_PASSWORD.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PASSWORD"))
                .andDo(print());
    }
}
