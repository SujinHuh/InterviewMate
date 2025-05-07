package com.interviewmate.interview.service.gpt;

import com.interviewmate.interview.service.model.AiChatResponse;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

public interface GptClient {
    AiChatResponse generate(List<Message> messages);

    String generateQuestion(String topic);

    String generateFeedback(String answer);
}
