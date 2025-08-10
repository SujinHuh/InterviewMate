package com.interviewmate.interview.service.gpt;

import com.interviewmate.interview.domain.Answer;
import com.interviewmate.interview.domain.Feedback;
import com.interviewmate.interview.domain.InterviewQuestion;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AiPromptBuilder {

    public List<Message> buildPrompt(InterviewQuestion question, Answer answer, Feedback feedback) {
        List<Message> promptMessages = new ArrayList<>();

        promptMessages.add(new SystemMessage(
                "너는 10년차 IT기업 백엔드 면접관이야. 아래는 한 지원자의 답변과 이전 피드백이야.\n" +
                        "이전 피드백을 반영해서 다음 면접 질문을 생성해줘.\n" +
                        "질문은 직무 적합성을 평가할 수 있도록 기술적으로 구체적이고, 핵심 역량을 검증할 수 있는 방향으로 작성해줘.\n" +
                        "한 번에 하나의 질문만 작성해줘."
        ));

        promptMessages.add(new UserMessage("이전 질문: " + question.getContent()));
        promptMessages.add(new UserMessage("사용자 답변: " + answer.content()));
        promptMessages.add(new UserMessage("피드백: " + feedback.feedbackContent()));

        return promptMessages;
    }
}