package com.interviewmate.interview.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class AnswerResponseDTO {

    private String answerId;
    public AnswerResponseDTO(String answerId){
        this.answerId = answerId;
    }
}
