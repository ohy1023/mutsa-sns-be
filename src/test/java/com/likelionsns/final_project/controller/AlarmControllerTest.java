package com.likelionsns.final_project.controller;

import com.likelionsns.final_project.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AlarmController.class)
class AlarmControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AlarmService alarmService;

    @Test
    @DisplayName("알림 목록 조회 성공")
    @WithMockUser
    void viewAlarms() {


    }
}