package com.interviewmate.interview.controller.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequestDTO {

    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String userId;

    @NotBlank(message = "면접 주제는 필수입니다.")
    private String topic;

}
