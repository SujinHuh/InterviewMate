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

@WebMvcTest(InterviewController.class)
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterviewService interviewService;

    @Autowired
    private ObjectMapper objectMapper;

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