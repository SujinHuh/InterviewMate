package com.interviewmate.interview.service;

import com.interviewmate.interview.repository.InterviewMapper;
import com.interviewmate.interview.service.gpt.GptClient;
import com.interviewmate.interview.service.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private GptClient gptClient;
    @InjectMocks
    private InterviewServiceImpl interviewService;
    @Mock
    private InterviewMapper interviewMapper;

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

}