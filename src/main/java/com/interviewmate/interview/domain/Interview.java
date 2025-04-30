package com.interviewmate.interview.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class Interview {
    private String id;
    private String userId;
    private String topic;
    private String status;
    private boolean questionGenerated;
    private String finalFeedback;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}

