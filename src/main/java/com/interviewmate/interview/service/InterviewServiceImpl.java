package com.interviewmate.interview.service;


import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InterviewServiceImpl implements InterviewService {
    @Override
    public InterviewOutput createInterview(InterviewInput input) {

        String generatedId = UUID.randomUUID().toString();

        return new InterviewOutput(generatedId, input.getTopic());
    }

}
