package com.interviewmate.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.exception.InterviewNotFoundException;
import com.interviewmate.interview.controller.dto.InterviewRequest;
import com.interviewmate.interview.controller.dto.InterviewResponse;
import com.interviewmate.interview.controller.dto.QuestionResponse;
import com.interviewmate.interview.repository.InterviewMapper;
import com.interviewmate.interview.repository.InterviewQuestionMapper;
import com.interviewmate.interview.repository.UserMapper;
import com.interviewmate.interview.service.InterviewService;
import com.interviewmate.interview.service.model.InterviewOutput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InterviewController.class)
@AutoConfigureMockMvc
class InterviewControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InterviewService interviewService;

    @MockBean
    private InterviewMapper interviewMapper;

    @MockBean
    private UserMapper testUserMapper;

    @MockBean
    private InterviewQuestionMapper interviewQuestionMapper;

    @Test
    void createInterview_API정상호출() throws Exception {
        InterviewRequest request = InterviewRequest.builder()
                .userId("user-123")
                .topic("백엔드 개발")
                .build();

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

    @Test
    void createInterview_IDNull일때_에러처리() throws Exception {
        InterviewRequest request = InterviewRequest.builder()
                .userId("user-123")
                .topic("백엔드 개발")
                .build();

        InterviewOutput output = new InterviewOutput(null, "백엔드 개발");

        given(interviewService.createInterview(any())).willReturn(output);

        MvcResult result = mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andReturn();

        mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Interview Creation Failed"))
                .andExpect(jsonPath("$.message").value("생성된 인터뷰 ID가 null입니다."))
                .andExpect(jsonPath("$.status_code").value(500));
    }

    @Test
    void createInterview_유효성_실패시_400에러() throws Exception {
        InterviewRequest request = InterviewRequest.builder()
                .userId(null)
                .topic("백엔드 개발")
                .build();

        InterviewOutput output = new InterviewOutput(null, "백엔드 개발");

        given(interviewService.createInterview(any())).willReturn(output);

        MvcResult result = mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("사용자 ID는 필수입니다."))
                .andExpect(jsonPath("$.status_code").value(400));
    }

    @Test
    void createQuestions_존재하지않는_interviewId_에러처리() throws Exception {

        given(interviewService.getTopicByInterviewId("invalid-999"))
                .willThrow(new InterviewNotFoundException("해당 interviewId에 대한 정보가 없습니다."));

        String topic = "spring";
        InterviewRequest request = InterviewRequest.builder()
                .userId("user-123")
                .topic(topic)
                .build();

        String invalidInterviewId = "invalid-999";

        mockMvc.perform(post("/api/interviews/" + invalidInterviewId + "/questions"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("해당 interviewId에 대한 정보가 없습니다."))
                .andExpect(jsonPath("$.status_code").value(400));
    }
}