package com.interviewmate.interview.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
    private String id;
    private String content;
    private int questionOrder;
    private boolean isAnswered;
    private Timestamp createdAt;

    public QuestionResponse(String id, String content) {
        this.id = id;
        this.content = content;
    }
}


