package com.interviewmate.interview.service;

import com.interviewmate.interview.controller.dto.AnswerRequestDTO;
import com.interviewmate.interview.controller.dto.QuestionResponseDTO;
import com.interviewmate.interview.domain.Answer;
import com.interviewmate.interview.domain.Feedback;
import com.interviewmate.interview.domain.InterviewQuestion;
import com.interviewmate.interview.repository.AnswerMapper;
import com.interviewmate.interview.repository.FeedbackMapper;
import com.interviewmate.interview.repository.InterviewMapper;
import com.interviewmate.interview.repository.InterviewQuestionMapper;
import com.interviewmate.interview.service.gpt.AiPromptBuilder;
import com.interviewmate.interview.service.gpt.GptClient;
import com.interviewmate.interview.service.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private GptClient gptClient;
    @Mock
    private AnswerMapper answerMapper;
    @InjectMocks
    private InterviewServiceImpl interviewService;
    @Mock
    private InterviewMapper interviewMapper;
    @Mock
    InterviewQuestionMapper interviewQuestionMapper;
    @Mock
    private FeedbackMapper feedbackMapper;
    @Mock
    private AiPromptBuilder aiPromptBuilder;

    @Test
    void createInterview_200_OK() {

        InterviewInput input = new InterviewInput("user-123", "백엔드 개발");

        InterviewOutput output = interviewService.createInterview(input);

        assertNotNull(output.getInterviewId());
        assertEquals("백엔드 개발", output.getTopic());
    }

    @Test
    void generateQuestion_OK() {
        String topic = "Java의 동시성";

        AiChatMessage message = new AiChatMessage("동시성은 어떤 문제를 발생시키나요?");
        AiChatResult result = new AiChatResult(message);
        AiChatResponse mockedResponse = new AiChatResponse(result);

        given(gptClient.generate(anyList())).willReturn(mockedResponse);

        String generatedQuestion = interviewService.generateQuestion(topic);

        assertEquals("동시성은 어떤 문제를 발생시키나요?", generatedQuestion);
    }

    @Test
    void generateFeedback_OK() {

        String answer = "HTTP 는 상태를 유지하지 않는다.";
        AiChatMessage mockMessage = new AiChatMessage("좋은 답변이에요. 상태 유지 방식에 대해 구체적인 예시를 들어도 좋아요.");
        AiChatResult mockResult = new AiChatResult(mockMessage);
        AiChatResponse mockResponse = new AiChatResponse(mockResult);

        given(gptClient.generate(anyList())).willReturn(mockResponse);

        String feedback = interviewService.generateFeedback(answer);

        assertNotNull(feedback);
        assertEquals("좋은 답변이에요. 상태 유지 방식에 대해 구체적인 예시를 들어도 좋아요.", feedback);

    }

    @Test
    void submitAnswer_insertsAnswerAndReturnsId() {

        String interviewId = "intv-123";
        String questionId = "q-456";
        AnswerRequestDTO answerRequestDTO = new AnswerRequestDTO("user-123","사용자 답변 내용");

        String answerId = interviewService.submitAnswer(interviewId, questionId, answerRequestDTO);

        assertNotNull(answerId);

        ArgumentCaptor<Answer> captor = ArgumentCaptor.forClass(Answer.class);
        verify(answerMapper).insert(captor.capture());

        Answer saved = captor.getValue();

        assertEquals(questionId, saved.questionId());
        assertEquals(answerRequestDTO.content(), saved.content());
    }
    @Test
    void saveFeedback_givenValidAnswerId_fetchesAnswerAndInsertsFeedback() {

        String answerId = "user-123";

        when(answerMapper.findById(answerId))
                .thenReturn(
                        new Answer(
                                answerId,
                                "q-1",
                                "HTTP와 HTTPS 가 있습니다",
                                LocalDateTime.now(),
                                true
                        )
                );

        AiChatMessage message = new AiChatMessage("AI 요약 피드백입니다");
        AiChatResult result = new AiChatResult(message);
        AiChatResponse response = new AiChatResponse(result);
        when(gptClient.generate(anyList())).thenReturn(response);

        String saveFeedback = interviewService.saveFeedback(answerId);

        verify(gptClient).generate(anyList());

        ArgumentCaptor<Feedback> captor = ArgumentCaptor.forClass(Feedback.class);
        verify(feedbackMapper).insert(captor.capture());

        Feedback saved = captor.getValue();
        assertEquals("AI 요약 피드백입니다", saved.feedbackContent());

        assertNotNull(saveFeedback);
    }

    @Test
    void generateNextQuestion_정상호출_nextOrder2_finalFeedback_미호출() {

        InterviewQuestion lastQuestion = InterviewQuestion.builder()
                .id("question-id")
                .interviewId("interview-id")
                .content("Q1 내용")
                .questionOrder(1)
                .answered(true)
                .createdAt(LocalDateTime.now().minusMinutes(1))
                .build();

        Answer lastAnswer = new Answer(
                "answer-id", "question-id", "사용자 답변", LocalDateTime.now().minusSeconds(30), true
        );

        Feedback feedback = new Feedback(
                "feedback-id", "answer-id", "피드백 요약 내용", 0, null, LocalDateTime.now().minusSeconds(10)
        );

        when(interviewQuestionMapper.findLastAnsweredQuestion("interview-id"))
                .thenReturn(lastQuestion);

        when(answerMapper.findByQuestionId("question-id"))
                .thenReturn(lastAnswer);

        when(feedbackMapper.findByAnswerId("answer-id"))
                .thenReturn(feedback);

        List<Message> dummyMessages = List.of(new SystemMessage("dummy"));
        when(aiPromptBuilder.buildPrompt(lastQuestion, lastAnswer, feedback))
                .thenReturn(dummyMessages);

        AiChatResponse aiChatResponse = new AiChatResponse(new AiChatResult(new AiChatMessage("새 질문 내용")));
        when(gptClient.generate(dummyMessages)).thenReturn(aiChatResponse);

        ArgumentCaptor<InterviewQuestion> captor = ArgumentCaptor.forClass(InterviewQuestion.class);
        doNothing().when(interviewQuestionMapper).insert(captor.capture());

        when(interviewQuestionMapper.findLastAnsweredQuestion("interview-id"))
                .thenReturn(lastQuestion);
        QuestionResponseDTO result = interviewService.generateNextQuestion("interview-id");

        assertNotNull(result);
        assertEquals("새 질문 내용", result.getContent());
        assertEquals(2, result.getQuestionOrder());
        assertEquals(false, result.isAnswered());
    }


}