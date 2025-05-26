package com.interviewmate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger errorLogger = LoggerFactory.getLogger("com.interviewmate.exception");

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        errorLogger.error("JSON 파싱 에러 발생: {}", ex.getMessage(), ex);
        return buildErrorResponse("Bad Request", "요청 형식이 잘못되었습니다.", 400);
    }

    @ExceptionHandler(InterviewNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInterviewNotFound(InterviewNotFoundException ex) {
        errorLogger.error("인터뷰 찾을 수 없음: {}", ex.getMessage(), ex);
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

        errorLogger.error("유효성 검사 실패: {}", message, ex);
        return buildErrorResponse("Bad Request", message, 400);
    }

    @ExceptionHandler(InterviewCreationException.class)
    public ResponseEntity<Map<String, Object>> handleInterviewCreationError(InterviewCreationException ex) {
        errorLogger.error("인터뷰 생성 실패: {}", ex.getMessage(), ex);
        return buildErrorResponse("Interview Creation Failed", ex.getMessage(), 500);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String error, String message, int statusCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", error);
        body.put("message", message);
        body.put("status_code", statusCode);

        return new ResponseEntity<>(body, HttpStatus.valueOf(statusCode));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnhandledException(Exception ex) {
        errorLogger.error("처리되지 않은 예외 발생: {}", ex.getMessage(), ex);
        return buildErrorResponse("Internal Server Error", "서버 내부 에러가 발생했습니다.", 500);
    }
}