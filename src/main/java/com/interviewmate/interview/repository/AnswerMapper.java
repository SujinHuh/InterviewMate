package com.interviewmate.interview.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

@Mapper
public interface AnswerMapper {
    int insertAnswer(@Param("id") UUID id,
                     @Param("questionId") UUID questionId,
                     @Param("content") String content,
                     @Param("is_submitted") boolean isSubmitted);
}
