package com.interviewmate.interview.service;


import com.interviewmate.interview.service.gpt.GptClient;
import com.interviewmate.interview.service.model.ChatResponse;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.DoubleStream;

@Service
public class InterviewServiceImpl implements InterviewService {
    private final GptClient gptClient;

    public InterviewServiceImpl(GptClient gptClient) {
        this.gptClient = gptClient;
    }
    @Override
    public InterviewOutput createInterview(InterviewInput input) {

        String generatedId = UUID.randomUUID().toString();

        return new InterviewOutput(generatedId, input.getTopic());
    }

    @Override
    public String generateQuestion(String topic) {
        List<Message> messages = List.of(
                new SystemMessage("너는 면접관이야. 주어진 주제에 대해 하나의 면접 질문을 만들어줘."),
                new UserMessage("주제: " + topic)
        );
        ChatResponse response = gptClient.generate(messages);
        return response.getResult().getOutput().getContent();
    }

    @Override
    public String generateFeedback(String answer) {
        List<Message> messages = List.of(
                new SystemMessage("너는 면접관이야. 아래 면접 답변에 대한 간단한 피드백을 제공해줘."),
                new UserMessage("답변 : " + answer)
        );
        ChatResponse response = gptClient.generate(messages);
        return response.getResult().getOutput().getContent();
    }


}
