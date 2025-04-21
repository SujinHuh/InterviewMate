package com.interviewmate.interview.service.gpt;

import com.interviewmate.interview.service.model.ChatOutput;
import com.interviewmate.interview.service.model.ChatRequest;
import com.interviewmate.interview.service.model.ChatResponse;
import com.interviewmate.interview.service.model.ChatResult;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GptClientImpl implements GptClient{
    private final OpenAiChatClient chatClient;

    public GptClientImpl(OpenAiChatClient chatClient){
        this.chatClient = chatClient;
    }

    @Override
    public ChatResponse generate(List<Message> messages) {
        org.springframework.ai.chat.ChatResponse springResponse = chatClient.call(new Prompt(messages));
        return mapFrom(springResponse);
    }
    @Override
    public String generateQuestion(String topic) {
        List<Message> messages = List.of(
                new SystemMessage("너는 면접관이야. 주어진 주제에 대해 하나의 면접 질문을 만들어줘."),
                new UserMessage("주제: " + topic)
        );

        ChatResponse response = mapFrom(chatClient.call(new Prompt(messages)));
        return response.getResult().getOutput().getContent();
    }

    @Override
    public String generateFeedback(String answer) {

        List<Message> messages = List.of(
                new SystemMessage("너는 면접관이야. 아래 면접 답변에 대한 간단한 피드백을 제공해줘."),
                new UserMessage("답변 : " + answer)
        );

       ChatResponse response = mapFrom(chatClient.call(new Prompt(messages)));
        return response.getResult().getOutput().getContent();
    }

    private ChatResponse mapFrom(org.springframework.ai.chat.ChatResponse springResponse){

        ChatOutput output = new ChatOutput(springResponse.getResult().getOutput().getContent());
        ChatResult result = new ChatResult(output);
        return new ChatResponse(result);

    }
}
