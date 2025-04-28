package com.interviewmate.interview.repository;

import com.interviewmate.interview.domain.TestUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface TestUserMapper {

    @Insert("""
      INSERT INTO test_user (name)
      VALUES (#{name})
      """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(TestUser user);
}