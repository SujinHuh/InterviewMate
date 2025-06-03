package com.interviewmate.interview.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDTO {
    private String id;
    private String content;
    private int questionOrder;
    private boolean isAnswered;
    private Timestamp createdAt;

    public QuestionResponseDTO(String id, String content) {
        this.id = id;
        this.content = content;
    }
}
