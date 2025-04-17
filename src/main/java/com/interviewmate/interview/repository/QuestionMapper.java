package com.interviewmate.interview.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

@Mapper
public interface QuestionMapper {
    int insertQuestion(@Param("id") UUID id,
                       @Param("interviewId") UUID interviewId,
                       @Param("question") String question);

}
