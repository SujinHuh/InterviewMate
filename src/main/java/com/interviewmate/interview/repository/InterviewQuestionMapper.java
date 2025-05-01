package com.interviewmate.interview.repository;

import com.interviewmate.interview.domain.InterviewQuestion;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InterviewQuestionMapper {

    @Insert("""
            INSERT INTO questions
              (id, interview_id, content, question_order, is_answered, created_at)
            VALUES
              (#{id}, #{interviewId}, #{content}, #{questionOrder}, #{answered}, #{createdAt})
            """)
    void insert(InterviewQuestion question);

    @Select("""
            SELECT
              id,
              interview_id     AS interviewId,
              content,
              question_order   AS questionOrder,
              is_answered      AS answered,
              created_at       AS createdAt
            FROM questions
            WHERE interview_id = #{interviewId}
            ORDER BY question_order
            """)
    List<InterviewQuestion> findByInterviewId(String interviewId);
}