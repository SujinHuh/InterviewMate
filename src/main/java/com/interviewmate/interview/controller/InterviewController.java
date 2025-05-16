package com.interviewmate.interview.controller;

import com.interviewmate.exception.InterviewCreationException;
import com.interviewmate.interview.controller.dto.*;
import com.interviewmate.interview.service.InterviewService;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;
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
    public ResponseEntity<InterviewResponse> createInterview(@Valid @RequestBody InterviewRequest request) {

        InterviewInput input = new InterviewInput(request.getUserId(), request.getTopic());

        InterviewOutput output = interviewService.createInterview(input);

        if (output.getInterviewId() == null) {
            throw new InterviewCreationException("생성된 인터뷰 ID가 null입니다.");
        }

        InterviewResponse response = new InterviewResponse(output.getInterviewId(), output.getTopic());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{interviewId}/questions")
    public ResponseEntity<QuestionResponse> createQuestions(@PathVariable String interviewId) {

        String topic = interviewService.getTopicByInterviewId(interviewId);

        String question = interviewService.generateQuestion(topic);

        String questionId = interviewService.saveQuestion(interviewId, question);

        QuestionResponse response = new QuestionResponse(questionId,question);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{interviewId}/questions/{questionId}/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AnswerResponse> submitAnswerAndFeedback(@PathVariable String interviewId, @PathVariable String questionId, @Valid @RequestBody AnswerRequest answerRequest) {

        String answerId = interviewService.submitAnswer(interviewId, questionId, answerRequest);

        URI location = buildAnswerLocation(interviewId, questionId, answerId);

        return ResponseEntity
                .created(location)
                .body(new AnswerResponse(answerId));
    }

    private URI buildAnswerLocation(String interviewId, String questionId, String answerId) {
        return URI.create("/api/interviews/" + interviewId
                + "/questions/" + questionId
                + "/answers/" + answerId);
    }
}
