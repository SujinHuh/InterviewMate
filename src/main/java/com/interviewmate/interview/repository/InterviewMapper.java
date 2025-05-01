package com.interviewmate.interview.repository;

import com.interviewmate.interview.domain.Interview;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface InterviewMapper {
    @Insert("""
              INSERT INTO interviews
                (id, user_id, topic, status, question_generated, final_feedback, created_at, updated_at)
              VALUES
                (#{id}, #{userId}, #{topic}, #{status}, #{questionGenerated}, #{finalFeedback}, NOW(), NOW())
            """)
    void insert(Interview interview);

    @Select("""
              SELECT
                id,
                user_id    AS userId,
                topic,
                status,
                question_generated AS questionGenerated,
                final_feedback     AS finalFeedback,
                created_at         AS createdAt,
                updated_at         AS updatedAt
              FROM interviews
              WHERE id = #{id}
            """)
    Interview findById(String id);
}