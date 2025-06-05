package com.interviewmate.interview.domain;

import java.time.LocalDateTime;

public record Question(
        String id,
        String interviewId,
        String content,
        int questionOrder,
        boolean isAnswered,
        LocalDateTime createdAt
) {}