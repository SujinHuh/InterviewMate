package com.interviewmate.interview.controller;

import com.interviewmate.exception.InterviewCreationException;
import com.interviewmate.interview.controller.dto.*;
import com.interviewmate.interview.service.InterviewService;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;


    @PostMapping
    @Operation(
            summary     = "인터뷰 생성",
            description = "사용자가 입력한 userId와 topic으로 새로운 인터뷰를 생성하고, 생성된 인터뷰의 ID와 topic을 반환합니다."
    )
    public ResponseEntity<InterviewResponseDTO> createInterview(@Valid @RequestBody InterviewRequestDTO request) {

        InterviewInput input = new InterviewInput(request.getUserId(), request.getTopic());

        InterviewOutput output = interviewService.createInterview(input);

        if (output.getInterviewId() == null) {
            throw new InterviewCreationException("생성된 인터뷰 ID가 null입니다.");
        }

        InterviewResponseDTO interviewResponse = new InterviewResponseDTO(output.getInterviewId(), output.getTopic());

        return ResponseEntity.ok(interviewResponse);
    }

    @PostMapping("/{interviewId}/questions")
    @Operation(
            summary     = "면접 질문 생성",
            description = "주어진 interviewId의 인터뷰 토픽을 조회한 뒤, 해당 토픽으로 새로운 질문을 생성하고 생성된 질문의 ID와 내용을 반환합니다."
    )
    public ResponseEntity<QuestionResponseDTO> createQuestions(@PathVariable String interviewId) {

        String topic = interviewService.getTopicByInterviewId(interviewId);

        String question = interviewService.generateQuestion(topic);

        String questionId = interviewService.saveQuestion(interviewId, question);

        QuestionResponseDTO questionResponse = QuestionResponseDTO.builder()
                .id(questionId)
                .content(question)
                .build();

        return ResponseEntity.ok(questionResponse);
    }

    @PostMapping("/{interviewId}/questions/{questionId}/answers")
    @Operation(
            summary     = "답변 제출",
            description = "주어진 interviewId와 questionId에 대해 사용자가 제출한 답변을 저장하고, 생성된 답변의 ID를 반환합니다."
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AnswerResponseDTO> submitAnswerAndFeedback(@PathVariable String interviewId, @PathVariable String questionId, @Valid @RequestBody AnswerRequestDTO answerRequestDTO) {

        String answerId = interviewService.submitAnswer(interviewId, questionId, answerRequestDTO);

        URI location = buildAnswerLocation(interviewId, questionId, answerId);

        return ResponseEntity
                .created(location)
                .body(new AnswerResponseDTO(answerId));
    }

    private URI buildAnswerLocation(String interviewId, String questionId, String answerId) {
        return URI.create("/api/interviews/" + interviewId
                + "/questions/" + questionId
                + "/answers/" + answerId);
    }


    @PostMapping("/{interviewId}/questions/{questionId}/answers/{answerId}/feedback")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary     = "피드백 저장",
            description = "사용자가 제출한 답변(answerId)에 대해 AI가 생성한 피드백을 저장하고, 저장된 피드백의 ID를 반환합니다."
    )
    public FeedbackResponseDTO submitFeedback(@PathVariable String interviewId, @PathVariable String questionId, @PathVariable String answerId, @Valid @RequestBody FeedbackRequestDTO request
    ) {
        String feedbackId = interviewService.submitFeedback(interviewId, questionId, answerId, request);

        return new FeedbackResponseDTO(
                feedbackId,
                "피드백이 성공적으로 저장되었습니다."
        );
    }

    @PostMapping("{interviewId}/questions/next")
    @Operation(
            summary     = "다음 질문 생성",
            description = "주어진 interviewId의 인터뷰에서 마지막 질문, 답변, 피드백을 바탕으로 새로운 면접 질문을 생성하고, 생성된 질문의 ID와 내용을 반환합니다."
    )
    public ResponseEntity<QuestionResponseDTO> generateNextQuestion(@PathVariable String interviewId) {

        QuestionResponseDTO response = interviewService.generateNextQuestion(interviewId);

        return ResponseEntity.ok(response);
    }
}
