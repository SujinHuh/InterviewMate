package com.interviewmate.interview.service.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.ai.chat.messages.Message;
import java.util.List;
@Getter
@Builder
public class ChatRequest {
    private String topic;
    private List<Message> messages;
}
