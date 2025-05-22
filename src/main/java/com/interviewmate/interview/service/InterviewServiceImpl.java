package com.interviewmate.interview.service;

import com.interviewmate.interview.controller.dto.AnswerRequest;
import com.interviewmate.interview.domain.Answer;
import com.interviewmate.interview.domain.Feedback;
import com.interviewmate.interview.domain.Interview;
import com.interviewmate.interview.domain.InterviewQuestion;
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
        logger.info("⏱️ interview 저장 시간: {}ms", elapsed);

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
        logger.info("🌐 GPT 질문 생성 시간: {}ms", gptElapsed);

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
        logger.info("⏱️ GPT 피드백 생성 시간: {}ms", elapsed);
        return response.result().output().content();
    }

    @Override
    public String getTopicByInterviewId(String interviewId) {

        Interview interview = interviewMapper.findById(interviewId);

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
        logger.info("🗃️ question 저장 시간: {}ms", elapsed);

        return qusetionId;
    }

    @Override
    public String submitAnswer(String interviewId, String questionId, AnswerRequest answerRequest) {

        String answerId = UUID.randomUUID().toString();

        Answer answer = new Answer(
                answerId,
                questionId,
                answerRequest.content(),
                LocalDateTime.now(),
                true
        );

        long start = System.currentTimeMillis();
        answerMapper.insert(answer);
        long elapsed = System.currentTimeMillis() - start;
        logger.info("🗃️ answer 저장 시간: {}ms", elapsed);

        return answerId;
    }

    @Override
    public String saveFeedback(String answerId) {

        Answer answer = answerMapper.findById(answerId);

        long gptStart = System.currentTimeMillis();
        String feedbackContent = generateFeedback(answer.content());
        long gptElapsed = System.currentTimeMillis() - gptStart;
        logger.info("⏱️ GPT 피드백 생성 시간: {}ms", gptElapsed);

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
        logger.info("🗃️ feedback 저장 시간: {}ms", dbElapsed);

        return feedbackId;
    }


}
