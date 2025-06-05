package com.interviewmate.interview.controller;

import ch.qos.logback.core.model.processor.PhaseIndicator;
import com.interviewmate.exception.InterviewCreationException;
import com.interviewmate.interview.controller.dto.*;
import com.interviewmate.interview.domain.Question;
import com.interviewmate.interview.service.InterviewService;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    @Operation(
            description = "Start new interview"
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
            description = "Generate interview question"
    )
    public ResponseEntity<QuestionResponseDTO> createQuestions(@PathVariable String interviewId) {

        String topic = interviewService.getTopicByInterviewId(interviewId);

        String question = interviewService.generateQuestion(topic);

        String questionId = interviewService.saveQuestion(interviewId, question);

        QuestionResponseDTO questionResponse = new QuestionResponseDTO(questionId, question);

        return ResponseEntity.ok(questionResponse);
    }

    @PostMapping("/{interviewId}/questions/{questionId}/answers")
    @Operation(
            description = "Submit only answer"
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

    @PostMapping("{interviewId}/questions/next")
    @Operation(
            description = "Generate next interview question"
    )
    public ResponseEntity<QuestionResponseDTO> generateNextQuestion(@PathVariable String interviewId) {

        Question nextQuestion = interviewService.generateNextQuestion(interviewId);
        QuestionResponseDTO nextQuestionResponse = new QuestionResponseDTO(nextQuestion.id(), nextQuestion.content());

        return ResponseEntity.ok(nextQuestionResponse);
    }
}
