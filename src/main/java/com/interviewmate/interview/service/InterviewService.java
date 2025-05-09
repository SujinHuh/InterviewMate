package com.interviewmate.interview.service;

import com.interviewmate.interview.controller.dto.AnswerRequest;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;

public interface InterviewService {
    InterviewOutput createInterview(InterviewInput input);

    String generateQuestion(String topic);

    String generateFeedback(String answer);

    String getTopicByInterviewId(String interviewId);

    String saveQuestion(String interviewId, String question);

    String submitAnswer(String interviewId, String questionId, AnswerRequest answer);

    String saveFeedback(String answerId);
}