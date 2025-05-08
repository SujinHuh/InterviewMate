package com.interviewmate.interview.repository;

import com.interviewmate.interview.domain.Feedback;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

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
}