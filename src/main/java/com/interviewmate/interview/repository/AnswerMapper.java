package com.interviewmate.interview.repository;

import com.interviewmate.interview.domain.Answer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AnswerMapper {
    @Insert("""
            INSERT INTO answer (
              id,
              question_id,
              content,
              submitted_at,
              is_submitted
            ) VALUES (
              #{id},
              #{questionId},
              #{content},
              #{submittedAt},
              #{isSubmitted}
            )
            """)
    void insert(Answer answer);

    @Select("""
            SELECT
              id,
              question_id   AS questionId,
              content,
              submitted_at  AS submittedAt,
              is_submitted  AS isSubmitted
            FROM answer
            WHERE id = #{id}
            """)
    Answer findById(String id);

    @Select("""
                SELECT 
                    id,
                    question_id AS questionId,
                    content,
                    created_at AS createdAt,
                    is_final AS isFinal
                FROM answers
                WHERE question_id = #{questionId}
            """)
    Answer findByQuestionId(String questionId);
}