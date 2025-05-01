package com.interviewmate.interview.controller;

import com.interviewmate.interview.controller.dto.InterviewRequest;
import com.interviewmate.interview.controller.dto.InterviewResponse;
import com.interviewmate.interview.controller.dto.QuestionResponse;
import com.interviewmate.interview.service.InterviewService;
import com.interviewmate.interview.service.model.InterviewInput;
import com.interviewmate.interview.service.model.InterviewOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(@RequestBody InterviewRequest request) {

        InterviewInput input = new InterviewInput(request.getUserId(), request.getTopic());

        InterviewOutput output = interviewService.createInterview(input);

        InterviewResponse response = new InterviewResponse(output.getInterviewId(), output.getTopic());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{interviewId}/questions")
    public ResponseEntity<QuestionResponse> createQuestions(@PathVariable String interviewId) {


        String topic = interviewService.getTopicByInterviewId(interviewId);

        String question = interviewService.generateQuestion(topic);

        interviewService.saveQuestion(interviewId, question);

        QuestionResponse response = new QuestionResponse(question);

        return ResponseEntity.ok(response);
    }
}
