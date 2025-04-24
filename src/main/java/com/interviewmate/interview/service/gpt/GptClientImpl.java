package com.interviewmate.interview.service.gpt;

import com.interviewmate.interview.service.model.AiChatMessage;
import com.interviewmate.interview.service.model.AiChatResponse;
import com.interviewmate.interview.service.model.AiChatResult;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GptClientImpl implements GptClient{
    private final OpenAiChatClient chatClient;

    @Override
    public AiChatResponse generate(List<Message> messages) {
        ChatResponse gptResponse = chatClient.call(new Prompt(messages));
        return mapFrom(gptResponse);
    }
    @Override
    public String generateQuestion(String topic) {
        List<Message> messages = List.of(
                new SystemMessage("너는 10년차 IT기업 백엔드 개발자 면접관이야. 아래는 한 지원자가 기술 면접에서 한 답변이야.\n" +
                        "답변을 읽고 아래 기준에 따라 간단하고 명확한 피드백을 제공해줘.\n" +
                        "1. 논리적 구조 2. 기술 이해도 3. 핵심 전달력\n" +
                        "각 항목별로 1~2문장 이내로, 지원자가 성장할 수 있도록 구체적이고 긍정적인 언어로 작성해줘.\n"),
                new UserMessage("주제: " + topic)
        );

        AiChatResponse response = mapFrom(chatClient.call(new Prompt(messages)));
        return response.getResult().getOutput().getContent();
    }

    @Override
    public String generateFeedback(String answer) {

        List<Message> messages = List.of(
                new SystemMessage("너는 10년차 IT기업 백엔드 개발자 면접관이야. 아래는 주제야." +
                        "해당 주제를 바탕으로 기술 면접에서 사용할 질문을 하나 만들어줘." +
                        "질문은 명확하고 기술적인 관점에서 작성해줘."),
                new UserMessage("답변 : " + answer)
        );

       AiChatResponse response = mapFrom(chatClient.call(new Prompt(messages)));
        return response.getResult().getOutput().getContent();
    }

    private AiChatResponse mapFrom(org.springframework.ai.chat.ChatResponse springResponse){

        AiChatMessage output = new AiChatMessage(springResponse.getResult().getOutput().getContent());
        AiChatResult result = new AiChatResult(output);
        return new AiChatResponse(result);

    }
}
