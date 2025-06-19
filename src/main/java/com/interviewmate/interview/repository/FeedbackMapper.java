package com.interviewmate.interview.repository;

import com.interviewmate.interview.domain.Feedback;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FeedbackMapper {
    @Insert("""
            INSERT INTO feedback (
              id,
              answer_id,
              per_answer_feedback,
              score,
              keyword_highlight,
              created_at
            ) VALUES (
              #{id},
              #{answerId},
              #{perAnswerFeedback},
              #{score},
              #{keywordHighlight},
              #{createdAt}
            )
            """)
    void insert(Feedback feedback);

    @Select("""
                SELECT 
                    id,
                    answer_id,
                    per_answer_feedback,
                    score,
                    keyword_highlight,
                    created_at
                FROM feedback
                WHERE answer_id = #{answerId}
            """)
    Feedback findByAnswerId(String answerId);

}