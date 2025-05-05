package com.interviewmate.interview.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
public class QuestionResponse {

    private String id;
    private String content;
    private int questionOrder;
    private boolean isAnswered;
    private Timestamp createdAt;

    public QuestionResponse(String content) {
        this.content = content;
    }

}
