package com.interviewmate.interview.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class AnswerResponse {

    private String answerId;
    public AnswerResponse (String answerId){
        this.answerId = answerId;
    }
}
