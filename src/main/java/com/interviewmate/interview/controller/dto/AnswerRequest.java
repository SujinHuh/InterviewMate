package com.interviewmate.interview.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerRequest {

    @NotBlank (message = "답변은 필수입니다.")
    @Size(max = 1000, message = "답변은 1000자 이내여야 합니다.")
    private String answer;
}
