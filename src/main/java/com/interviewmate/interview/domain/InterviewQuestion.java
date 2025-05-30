package com.interviewmate.interview.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestion {
    private String id;
    private String interviewId;
    private String content;
    private int questionOrder;
    private boolean answered;
    private Timestamp createdAt;
}