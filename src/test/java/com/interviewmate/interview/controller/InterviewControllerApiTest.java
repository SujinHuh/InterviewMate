package com.interviewmate.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.exception.InterviewNotFoundException;
import com.interviewmate.interview.controller.dto.AnswerRequestDTO;
import com.interviewmate.interview.controller.dto.InterviewRequestDTO;
import com.interviewmate.interview.repository.*;
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

    @MockBean
    private AnswerMapper answerMapper;

    @MockBean
    private FeedbackMapper feedbackMapper;

    @Test
    void createInterview_API정상호출() throws Exception {
        InterviewRequestDTO request = InterviewRequestDTO.builder()
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
        InterviewRequestDTO request = InterviewRequestDTO.builder()
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
        InterviewRequestDTO request = InterviewRequestDTO.builder()
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
        InterviewRequestDTO request = InterviewRequestDTO.builder()
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

    @Test
    void createInterview_topic이_null이면_400에러() throws Exception {

        InterviewRequestDTO request = InterviewRequestDTO.builder()
                .userId("user-123")
                .topic(null)
                .build();

        mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("면접 주제는 필수입니다."))
                .andExpect(jsonPath("$.status_code").value(400));
    }

    @Test
    void saveAnswer_정상동작_답변과피드백ID반환() throws Exception {

        AnswerRequestDTO request = new AnswerRequestDTO(
                "user-123",
                "HTTPS는 HTTP보다 보안성이 강화된 프로토콜입니다."
        );


        given(interviewService.submitAnswer(any(), any(), any())).willReturn("answer-123");
        given(interviewService.saveFeedback(any())).willReturn("feedback-456");


        mockMvc.perform(post("/api/interviews/interview-1/questions/question-1/answers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.answerId").value("answer-123"));
    }
}