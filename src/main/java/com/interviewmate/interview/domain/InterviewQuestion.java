package com.interviewmate.interview.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestion {
    private String id;
    private String interviewId;
    private String content;
    private int questionOrder;
    private boolean answered;
    private LocalDateTime createdAt;

}