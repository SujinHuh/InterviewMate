package com.interviewmate.interview.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.UUID;

@Mapper
public interface InterviewMapper {
    int insertInterview(@Param("id") UUID id,
                        @Param("userId") UUID userId,
                        @Param("topic") String topic);
    String findTopicById(@Param("id") UUID interviewId);


}
