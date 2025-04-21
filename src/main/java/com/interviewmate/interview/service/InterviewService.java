package com.interviewmate.interview.service;

import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;

public interface InterviewService {
    InterviewOutput createInterview(InterviewInput input);
    String generateQuestion(String topic);
    String generateFeedback(String answer);

}