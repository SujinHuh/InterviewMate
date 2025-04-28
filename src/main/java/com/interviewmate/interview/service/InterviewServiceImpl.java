package com.interviewmate.interview.service;


import com.interviewmate.interview.service.gpt.GptClient;
import com.interviewmate.interview.service.model.AiChatResponse;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {
    private final GptClient gptClient;

    @Override
    public InterviewOutput createInterview(InterviewInput input) {

        String generatedId = UUID.randomUUID().toString();

        return new InterviewOutput(generatedId, input.getTopic());
    }

    @Override
    public String generateQuestion(String topic) {
        List<Message> messages = List.of(
                new SystemMessage("너는 10년차 IT기업 백엔드 개발자 면접관이야. 아래는 주제야." +
                        "해당 주제를 바탕으로 기술 면접에서 사용할 질문을 하나 만들어줘." +
                        "질문은 명확하고 기술적인 관점에서 작성해줘."),
                new UserMessage("주제: " + topic)
        );
        AiChatResponse response = gptClient.generate(messages);
        return response.result().output().content();
    }

    @Override
    public String generateFeedback(String answer) {
        List<Message> messages = List.of(
                new SystemMessage("너는 10년차 IT기업 백엔드 개발자 면접관이야. 아래는 한 지원자가 기술 면접에서 한 답변이야.\n" +
                        "답변을 읽고 아래 기준에 따라 간단하고 명확한 피드백을 제공해줘.\n" +
                        "1. 논리적 구조 2. 기술 이해도 3. 핵심 전달력\n" +
                        "각 항목별로 1~2문장 이내로, 지원자가 성장할 수 있도록 구체적이고 긍정적인 언어로 작성해줘.\n"),
                new UserMessage("답변 : " + answer)
        );
        AiChatResponse response = gptClient.generate(messages);
        return response.result().output().content();
    }


}
