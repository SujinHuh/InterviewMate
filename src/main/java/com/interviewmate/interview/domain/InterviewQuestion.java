package com.interviewmate.interview.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InterviewQuestion {
    private final String question;
    public String getQuestion(){
        return question;
    }
}
