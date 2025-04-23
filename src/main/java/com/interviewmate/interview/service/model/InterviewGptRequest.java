package com.interviewmate.interview.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.ai.chat.messages.Message;
import java.util.List;
@Getter
@Builder
@AllArgsConstructor
public final class InterviewGptRequest {
    private String topic;
    private List<Message> messages;
}
