package com.interviewmate.interview.domain;

import java.time.LocalDateTime;

public record Feedback(
    String id,
    String answerId,
    String perAnswerFeedback,
    int score,
    String keywordHighlight,
    LocalDateTime createdAt
) {}
