package com.interviewmate.interview.repository;

import com.interviewmate.interview.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional          // → 테스트 끝나고 롤백
@Sql(scripts = "/test-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void 사용자등록_테스트() {
        User user = new User();
        user.setId("user-1234");
        user.setNickname("테스트유저");
        user.setGuest(false);

        userMapper.insert(user);

        // insert 메서드 호출 후 user.getId()가 변했으면(=키 생성되었으면) 통과
        assertThat(user.getId()).isNotNull();
    }
}