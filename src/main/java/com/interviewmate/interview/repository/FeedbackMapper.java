package com.interviewmate.interview.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

@Mapper
public interface FeedbackMapper {
    int insertFeedback(@Param("id") UUID id,
                       @Param("feedbackId") UUID feedbackId,
                       @Param("content") String content);
}
