package com.interviewmate.interview.domain;

import java.time.LocalDateTime;

public record Answer(
        String id,
        String questionId,
        String content,
        LocalDateTime submittedAt,
        boolean isSubmitted
) {
}
