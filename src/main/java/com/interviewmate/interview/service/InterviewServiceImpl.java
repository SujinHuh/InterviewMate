package com.interviewmate.interview.service;

import com.interviewmate.exception.InterviewNotFoundException;
import com.interviewmate.interview.controller.dto.AnswerRequestDTO;
import com.interviewmate.interview.controller.dto.FeedbackRequestDTO;
import com.interviewmate.interview.controller.dto.QuestionResponseDTO;
import com.interviewmate.interview.domain.*;
import com.interviewmate.interview.repository.AnswerMapper;
import com.interviewmate.interview.repository.FeedbackMapper;
import com.interviewmate.interview.repository.InterviewMapper;
import com.interviewmate.interview.repository.InterviewQuestionMapper;
import com.interviewmate.interview.service.gpt.AiPromptBuilder;
import com.interviewmate.interview.service.gpt.GptClient;
import com.interviewmate.interview.service.model.AiChatResponse;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
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
    private final AiPromptBuilder aiPromptBuilder;
    private static final Logger log = LoggerFactory.getLogger(InterviewServiceImpl.class);
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
                LocalDateTime.now()
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
    public String submitFeedback(String interviewId, String questionId, String answerId, FeedbackRequestDTO request){

        if(interviewQuestionMapper.findByInterviewId(questionId) == null) {
            throw new InterviewNotFoundException("존재하지 않는 질문입니다. questionId = " +  questionId );
        }
        if(answerMapper.findById(answerId) == null) {
            throw new InterviewNotFoundException("존재하지 않는 답변입니다. answerId = " + answerId );
        }
        List<Message> prompt = List.of(
                new SystemMessage("당신은 10년 차 백엔드 개발자 면접관입니다. 아래 지원자의 답변을 보고 구체적이고 긍정적인 피드백을 작성해주세요."+  "그런 다음, 생성된 피드백을 핵심 문장 1~2개로 요약해서 반환해 주세요."),
                new UserMessage("답변: " + request.content())
        );
        AiChatResponse aiChatResponse = gptClient.generate(prompt);
        String feedbackContent = aiChatResponse.result().output().content();

        String feedbackId = UUID.randomUUID().toString();
        Feedback feedback = new Feedback(feedbackId,answerId,feedbackContent,0,null,LocalDateTime.now());

        feedbackMapper.insert(feedback);

        interviewQuestionMapper.markAnswered(questionId);

        return feedbackId;

    }

    @Override
    public QuestionResponseDTO generateNextQuestion(String interviewId) {
        long startTime = System.currentTimeMillis();

        InterviewQuestion lastQuestion = interviewQuestionMapper.findLastAnsweredQuestion(interviewId);
        if (lastQuestion == null) {
            MDC.put("failReason", "이전 질문 없음");
            log.warn("generateNextQuestion 실패: interviewId={}, reason={}", interviewId, MDC.get("failReason"));
            throw new IllegalStateException("이전 질문이 존재하지 않습니다.");
        }

        Answer lastAnswer = answerMapper.findByQuestionId(lastQuestion.getId());
        if (lastAnswer == null) {
            MDC.put("failReason", "이전 질문에 대한 답변 없음");
            log.warn("generateNextQuestion 실패: interviewId={}, lastQuestionId={}, reason={}",
                    interviewId, lastQuestion.getId(), MDC.get("failReason"));
            throw new IllegalStateException("이전 질문에 대한 답변이 존재하지 않습니다.");
        }

        Feedback feedback = feedbackMapper.findByAnswerId(lastAnswer.id());
        if (feedback == null) {
            MDC.put("failReason", "피드백 없음");
            log.warn("generateNextQuestion 실패: interviewId={}, lastAnswerId={}, reason={}",
                    interviewId, lastAnswer.id(), MDC.get("failReason"));
            throw new IllegalStateException("피드백이 존재하지 않습니다.");
        }

        List<Message> messages = aiPromptBuilder.buildPrompt(lastQuestion, lastAnswer, feedback);

        long aiStart = System.currentTimeMillis();
        AiChatResponse response = gptClient.generate(messages);
        MDC.put("aiElapsed", String.valueOf(System.currentTimeMillis() - aiStart));

        String newQuestionContent = response.result().output().content();
        int nextOrder = lastQuestion.getQuestionOrder() + 1;

        InterviewQuestion newQuestion = InterviewQuestion.builder()
                .id(UUID.randomUUID().toString())
                .interviewId(interviewId)
                .content(newQuestionContent)
                .questionOrder(nextOrder)
                .answered(false)
                .createdAt(LocalDateTime.now())
                .build();

        long dbStart = System.currentTimeMillis();
        interviewQuestionMapper.insert(newQuestion);
        MDC.put("dbElapsed", String.valueOf(System.currentTimeMillis() - dbStart));

        if (nextOrder == 3) {
            generateFinalFeedback(interviewId);
        }

        MDC.put("totalElapsed", String.valueOf(System.currentTimeMillis() - startTime));

        log.info("generateNextQuestion 완료: interviewId={}, newQuestionId={}, questionOrder={}, aiElapsed={}ms, dbElapsed={}ms, totalElapsed={}ms",
                interviewId, newQuestion.getId(), nextOrder, MDC.get("aiElapsed"), MDC.get("dbElapsed"), MDC.get("totalElapsed"));

        MDC.clear();

        return QuestionResponseDTO.builder()
                .id(newQuestion.getId())
                .content(newQuestion.getContent())
                .questionOrder(newQuestion.getQuestionOrder())
                .isAnswered(newQuestion.isAnswered())
                .createdAt(Timestamp.valueOf(newQuestion.getCreatedAt()))
                .build();
    }

    private void generateFinalFeedback(String interviewId) {
        // TODO: 지금까지의 질문, 답변, 피드백을 인터뷰 ID 기준으로 모두 조회
        // TODO: GPT 프롬프트 구성 및 최종 피드백 생성 후 DB 저장
    }
}
