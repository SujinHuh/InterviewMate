package com.interviewmate.interview.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record FeedbackRequestDTO(
        @NotBlank
        String userId,

        @NotBlank
        String content
) { }