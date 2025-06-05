package com.interviewmate.interview.service;

import com.interviewmate.interview.controller.dto.AnswerRequestDTO;
import com.interviewmate.interview.domain.Question;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;

public interface InterviewService {
    InterviewOutput createInterview(InterviewInput input);

    String generateQuestion(String topic);

    String generateFeedback(String answer);

    String getTopicByInterviewId(String interviewId);

    String saveQuestion(String interviewId, String question);

    String submitAnswer(String interviewId, String questionId, AnswerRequestDTO answer);

    String saveFeedback(String answerId);

    Question generateNextQuestion(String interviewId);
}