package com.interviewmate.interview.service;

import com.interviewmate.interview.controller.dto.AnswerRequestDTO;
import com.interviewmate.interview.controller.dto.QuestionResponseDTO;
import com.interviewmate.interview.domain.*;
import com.interviewmate.interview.repository.AnswerMapper;
import com.interviewmate.interview.repository.FeedbackMapper;
import com.interviewmate.interview.repository.InterviewMapper;
import com.interviewmate.interview.repository.InterviewQuestionMapper;
import com.interviewmate.interview.service.gpt.GptClient;
import com.interviewmate.interview.service.model.AiChatResponse;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {
    private final GptClient gptClient;
    private final InterviewMapper interviewMapper;
    private final InterviewQuestionMapper interviewQuestionMapper;
    private final AnswerMapper answerMapper;
    private final FeedbackMapper feedbackMapper;
    private static final Logger logger = LoggerFactory.getLogger(InterviewServiceImpl.class);

    @Override
    public InterviewOutput createInterview(InterviewInput input) {

        String generatedId = UUID.randomUUID().toString();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Interview interview = new Interview(
                generatedId,
                input.getUserId(),
                input.getTopic(),
                "IN_PROGRESS",
                false,
                null,
                now,
                now
        );

        long start = System.currentTimeMillis();
        interviewMapper.insert(interview);
        long elapsed = System.currentTimeMillis() - start;
        MDC.put("dbElapsed", String.valueOf(elapsed));

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
        long gptStart = System.currentTimeMillis();
        AiChatResponse response = gptClient.generate(messages);
        long gptElapsed = System.currentTimeMillis() - gptStart;
        MDC.put("gptElapsed", String.valueOf(gptElapsed));

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
        long start = System.currentTimeMillis();
        AiChatResponse response = gptClient.generate(messages);
        long elapsed = System.currentTimeMillis() - start;
        MDC.put("gptElapsed", String.valueOf(elapsed));
        return response.result().output().content();
    }

    @Override
    public String getTopicByInterviewId(String interviewId) {

        long start = System.currentTimeMillis();

        Interview interview = interviewMapper.findById(interviewId);

        long dbElapsed = System.currentTimeMillis() - start;
        MDC.put("dbElapsed", String.valueOf(dbElapsed));

        return interview.topic();

    }

    @Override
    public String saveQuestion(String interviewId, String question) {

        String qusetionId = UUID.randomUUID().toString();

        Timestamp now = new Timestamp(System.currentTimeMillis());

        InterviewQuestion interviewQuestion = new InterviewQuestion(
                qusetionId,
                interviewId,
                question,
                1,
                false,
                now
        );
        long start = System.currentTimeMillis();
        interviewQuestionMapper.insert(interviewQuestion);
        long elapsed = System.currentTimeMillis() - start;
        MDC.put("dbElapsed", String.valueOf(elapsed));

        return qusetionId;
    }

    @Override
    public String submitAnswer(String interviewId, String questionId, AnswerRequestDTO answerRequestDTO) {

        String answerId = UUID.randomUUID().toString();

        Answer answer = new Answer(
                answerId,
                questionId,
                answerRequestDTO.content(),
                LocalDateTime.now(),
                true
        );

        long start = System.currentTimeMillis();
        answerMapper.insert(answer);
        long elapsed = System.currentTimeMillis() - start;
        MDC.put("dbElapsed", String.valueOf(elapsed));

        return answerId;
    }

    @Override
    public String saveFeedback(String answerId) {

        Answer answer = answerMapper.findById(answerId);

        long gptStart = System.currentTimeMillis();
        String feedbackContent = generateFeedback(answer.content());
        long gptElapsed = System.currentTimeMillis() - gptStart;
        MDC.put("gptElapsed", String.valueOf(gptElapsed));

        String feedbackId = UUID.randomUUID().toString();


        Feedback feedback = new Feedback(
                feedbackId,
                answerId,
                feedbackContent,
                0,
                null,
                LocalDateTime.now()
        );

        long dbStart = System.currentTimeMillis();
        feedbackMapper.insert(feedback);
        long dbElapsed = System.currentTimeMillis() - dbStart;
        MDC.put("dbElapsed", String.valueOf(dbElapsed));

        return feedbackId;
    }

    @Override
    public Question generateNextQuestion(String interviewId) {

        InterviewQuestion lastQuestion = interviewQuestionMapper.findTopByInterviewIdOrderByQuestionOrderDesc(interviewId);

        if (lastQuestion == null) throw new IllegalStateException("이전 질문이 존재하지 않습니다.");

        Answer lastAnswer = answerMapper.findByQuestionId(lastQuestion.getId());
        if (lastAnswer == null) throw new IllegalStateException("이전 질문에 대한 답변이 존재하지 않습니다.");

        Feedback feedback = feedbackMapper.findByAnswerId(lastAnswer.id());
        if (feedback == null) throw new IllegalStateException("피드백이 존재하지 않습니다.");

        // 4. 프롬프트 구성 (Message 리스트 반환)

        // 5. GPT 호출 → 질문 생성

        // 6. 다음 질문 순서 계산

        // 7. 인터뷰 질문 도메인 객체 생성

        // 8. DB 저장

        // 9. 도메인 객체로 반환
        return null;
    }
}
