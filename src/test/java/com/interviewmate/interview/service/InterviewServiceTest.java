package com.interviewmate.interview.service;

import com.interviewmate.interview.controller.dto.InterviewRequest;
import com.interviewmate.interview.controller.dto.InterviewResponse;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InterviewServiceTest {

    InterviewService interviewService = new InterviewServiceImpl();// 수동 생성

    @Test
    void createInterview_200_OK(){
        //Given
        InterviewInput input = new InterviewInput("백엔드 개발");

        //When
        InterviewOutput output = interviewService.createInterview(input);

        //Then
        assertNotNull(output.getInterviewId());
        assertEquals("백엔드 개발", output.getTopic());
    }
}