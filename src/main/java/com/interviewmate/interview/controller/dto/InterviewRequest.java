package com.interviewmate.interview.controller.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequest {

    private String userId;
    private String topic;

}
