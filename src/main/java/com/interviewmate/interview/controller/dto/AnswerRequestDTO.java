package com.interviewmate.interview.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerRequestDTO(
        @NotBlank(message = "사용자 ID는 필수입니다.") String userId,
        @NotBlank(message = "답변은 필수입니다.")
        @Size(max = 1000, message = "답변은 1000자 이내여야 합니다.") String content
) {}