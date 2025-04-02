package com.interviewmate.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.interview.controller.dto.InterviewRequest;
import com.interviewmate.interview.controller.dto.InterviewResponse;
import com.interviewmate.interview.service.InterviewService;
import com.interviewmate.interview.service.model.InterviewOutput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterviewController.class)// 컨트롤러 단위의 통합 테스트
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc; //가상의 요청을 보내주는 도구

    @MockBean
    private InterviewService interviewService; // service를 가짜 (mock)로 주입

    @Autowired
    private ObjectMapper objectMapper; // 객체 <-> JSON 변환기

    @Test
    void createInterview_API정상호출() throws Exception {

        InterviewRequest request = new InterviewRequest();
        request.setTopic("백엔드 개발");

        InterviewOutput output = new InterviewOutput("12345", "백엔드 개발");

        given(interviewService.createInterview(any())).willReturn(output);

        MvcResult result = mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andReturn();

        mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.interviewId").value("12345"))
                        .andExpect(jsonPath("$.topic").value("백엔드 개발"));
    }
}