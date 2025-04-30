package com.interviewmate.interview.repository;

import com.interviewmate.interview.domain.TestUser;
import com.interviewmate.interview.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;


@Mapper
public interface UserMapper {
    @Insert("""
      INSERT INTO users (id, nickname, is_guest, created_at)
      VALUES (#{id}, #{nickname}, #{isGuest}, NOW())
    """)
    void insert(User user);
}