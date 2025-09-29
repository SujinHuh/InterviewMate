package com.interviewmate.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewmate.interview.controller.dto.*;
import com.interviewmate.interview.domain.Feedback;
import com.interviewmate.interview.domain.User;
import com.interviewmate.interview.repository.FeedbackMapper;
import com.interviewmate.interview.repository.InterviewMapper;
import com.interviewmate.interview.repository.UserMapper;
import com.interviewmate.interview.service.gpt.GptClient;
import com.interviewmate.interview.service.model.AiChatMessage;
import com.interviewmate.interview.service.model.AiChatResponse;
import com.interviewmate.interview.service.model.AiChatResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@MapperScan("com.interviewmate.interview.repository")
@Sql(scripts = "/test-cleanup.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class InterviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InterviewMapper interviewMapper;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @MockBean GptClient gptClient;

    private final String userId = "user-123";

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId("user-123");
        user.setNickname("테스트유저");
        user.setGuest(false);
        userMapper.insert(user);

        when(gptClient.generate(anyList())).thenReturn(new AiChatResponse(new AiChatResult(new AiChatMessage("spring에 대해서 설명해 주세요. (mocked)"))));
        when(gptClient.generate(anyList())).thenReturn(resp("spring에 대해서 설명해 주세요. (mocked)"), resp("HTTPS는 안전해요! (mocked)"));

    }

    private AiChatResponse resp(String text) {return new AiChatResponse(new AiChatResult(new AiChatMessage(text)));}

    @Test
    void createInterview_DB까지저장확인() throws Exception {
        InterviewRequestDTO request = InterviewRequestDTO.builder()
                .userId("user-123")
                .topic("spring")
                .build();

        var mvcResult = mockMvc.perform(post("/api/interviews")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        InterviewResponseDTO resp = objectMapper.readValue(json, InterviewResponseDTO.class);
        String interviewId = resp.getInterviewId();

        var saved = interviewMapper.findById(interviewId);
        assertThat(saved).isNotNull();
        assertThat(saved.userId()).isEqualTo("user-123");
        assertThat(saved.topic()).isEqualTo("spring");
    }

    @Test
    void createQuestion_정상동작_인터뷰ID로_질문생성요청() throws Exception {
        String topic = "spring";
        InterviewRequestDTO request = InterviewRequestDTO.builder()
                .userId("user-123")
                .topic(topic)
                .build();

        var mvcResult = mockMvc.perform(post("/api/interviews")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        InterviewResponseDTO resp = objectMapper.readValue(json, InterviewResponseDTO.class);
        String interviewId = resp.getInterviewId();

        String url = "/api/interviews/" + interviewId + "/questions";

        var createQuestion = mockMvc.perform(post(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String questionResponse = createQuestion.getResponse().getContentAsString();
        QuestionResponseDTO response = objectMapper.readValue(questionResponse, QuestionResponseDTO.class);

        String question = response.getContent();
        assertThat(question).isNotNull();
        assertThat(question).isNotBlank();
        assertThat(question.toLowerCase()).contains(topic);
    }

    @Test
    void endToEndTest_답변과피드백까지정상작동() throws Exception {
        String topic = "spring";

        var createInterview = mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                InterviewRequestDTO.builder()
                                        .userId("user-123")
                                        .topic(topic)
                                        .build()
                        )))
                .andExpect(status().isOk())
                .andReturn();

        var interviewResp = objectMapper.readValue(
                createInterview.getResponse().getContentAsString(),
                InterviewResponseDTO.class
        );
        String interviewId = interviewResp.getInterviewId();

        var createQuestion = mockMvc.perform(post("/api/interviews/" + interviewId + "/questions"))
                .andExpect(status().isOk())
                .andReturn();

        var questionResp = objectMapper.readValue(
                createQuestion.getResponse().getContentAsString(),
                QuestionResponseDTO.class
        );
        String questionId = questionResp.getId();
        String question   = questionResp.getContent();

        assertThat(question).isNotBlank();
        assertThat(question.toLowerCase()).contains(topic); // "spring" 포함 확인

        var answerReq = new AnswerRequestDTO(
                "user-123",
                "HTTPS는 HTTP보다 보안성이 강화된 프로토콜입니다."
        );

        var createAnswer = mockMvc.perform(post("/api/interviews/" + interviewId
                        + "/questions/" + questionId + "/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        var answerResp = objectMapper.readValue(
                createAnswer.getResponse().getContentAsString(),
                AnswerResponseDTO.class
        );
        String answerId = answerResp.getAnswerId();
        assertThat(answerId).isNotBlank();

        var feedbackReq = new FeedbackRequestDTO("user-123", "테스트 답변 내용입니다.");
        mockMvc.perform(post("/api/interviews/" + interviewId
                        + "/questions/" + questionId
                        + "/answers/" + answerId + "/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackReq)))
                .andExpect(status().isCreated());

        Feedback feedback = feedbackMapper.findByAnswerId(answerId);
        assertThat(feedback).isNotNull();
        assertThat(feedback.feedbackContent()).isNotBlank();
        assertThat(feedback.feedbackContent().toLowerCase()).contains("https");
    }
}