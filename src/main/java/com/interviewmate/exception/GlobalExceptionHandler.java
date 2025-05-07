package com.interviewmate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        return buildErrorResponse("Bad Request", "요청 형식이 잘못되었습니다.", 400);
    }

    @ExceptionHandler(InterviewNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInterviewNotFound(InterviewNotFoundException ex) {
        return buildErrorResponse("Bad Request", ex.getMessage(), 400);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");

        return buildErrorResponse("Bad Request", message, 400);
    }

    @ExceptionHandler(InterviewCreationException.class)
    public ResponseEntity<Map<String, Object>> handleInterviewCreationError(InterviewCreationException ex) {
        return buildErrorResponse("Interview Creation Failed", ex.getMessage(), 500);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String error, String message, int statusCode) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", error);
        body.put("message", message);
        body.put("status_code", statusCode);

        return new ResponseEntity<>(body, HttpStatus.valueOf(statusCode));
    }

}
