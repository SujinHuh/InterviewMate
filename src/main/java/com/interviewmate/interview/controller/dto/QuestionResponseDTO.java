package com.interviewmate.interview.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponseDTO {
    private String id;
    private String content;
    private int questionOrder;
    private boolean isAnswered;
    private Timestamp createdAt;

}
