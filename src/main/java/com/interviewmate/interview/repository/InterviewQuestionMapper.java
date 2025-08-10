package com.interviewmate.interview.repository;

import com.interviewmate.interview.domain.InterviewQuestion;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface InterviewQuestionMapper {

    /** 1) 새 질문 저장 */
    @Insert("""
        INSERT INTO questions
          (id, interview_id, content, question_order, is_answered, created_at)
        VALUES
          (#{id}, #{interviewId}, #{content}, #{questionOrder}, #{answered}, #{createdAt})
        """)
    void insert(InterviewQuestion question);

    /** 2) 인터뷰 내 모든 질문 조회 (생성 순) */
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
        ORDER BY question_order ASC
        """)
    List<InterviewQuestion> findByInterviewId(String interviewId);

    /** 3) 첫 질문(제일 낮은 order) 하나만 조회 */
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
        ORDER BY question_order ASC
        LIMIT 1
        """)
    InterviewQuestion findTopByInterviewIdOrderByQuestionOrderDesc(String interviewId);

    /** 4) 질문에 답변 달린 상태로 표시 */
    @Update("""
        UPDATE questions
           SET is_answered = TRUE
         WHERE id = #{questionId}
        """)
    void markAnswered(String questionId);

    /** 5) 마지막으로 답변된 질문 하나만 조회 */
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
          AND is_answered = TRUE
        ORDER BY question_order DESC
        LIMIT 1
        """)
    InterviewQuestion findLastAnsweredQuestion(String interviewId);
}