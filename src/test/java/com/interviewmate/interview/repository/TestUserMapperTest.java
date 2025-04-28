package com.interviewmate.interview.repository;

import com.interviewmate.interview.domain.TestUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = com.interviewmate.InterviewMateApplication.class)
@Transactional
public class TestUserMapperTest {
    @Autowired
    private TestUserMapper mapper;

    @Test
    void 사용자등록_테스트() {
        TestUser user = new TestUser();
        user.setName("테스트유저");
        mapper.insert(user);
        assertThat(user.getId()).isNotNull();
    }
}