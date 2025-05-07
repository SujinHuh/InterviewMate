package com.interviewmate.interview.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

public record Interview(
        String id,
        String userId,
        String topic,
        String status,
        boolean questionGenerated,
        String finalFeedback,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}