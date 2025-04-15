package com.interviewmate.interview.service;

public interface GptClient {
    String generateQuestion(String topic);
    String generateFeedback(String answer);
}
