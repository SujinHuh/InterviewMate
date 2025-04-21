package com.interviewmate.interview.service;

import com.interviewmate.interview.controller.dto.InterviewRequest;
import com.interviewmate.interview.controller.dto.InterviewResponse;
import com.interviewmate.interview.service.gpt.GptClient;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class InterviewServiceTest {

    InterviewService interviewService;
    GptClient gptClient = mock(GptClient.class);

    @Test
    void createInterview_200_OK(){

        InterviewInput input = new InterviewInput("백엔드 개발");

        InterviewOutput output = interviewService.createInterview(input);

        assertNotNull(output.getInterviewId());
        assertEquals("백엔드 개발", output.getTopic());
    }


}