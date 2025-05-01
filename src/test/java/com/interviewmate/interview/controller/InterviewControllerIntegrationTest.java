package com.interviewmate.interview.controller;

import com.interviewmate.interview.controller.dto.InterviewRequest;
import com.interviewmate.interview.controller.dto.InterviewResponse;
import com.interviewmate.interview.domain.User;
import com.interviewmate.interview.repository.InterviewMapper;
import com.interviewmate.interview.repository.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@MapperScan("com.interviewmate.interview.repository")
@Sql(scripts = "/test-cleanup.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class InterviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InterviewMapper interviewMapper;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId("user-123");
        user.setNickname("테스트유저");
        user.setGuest(false);
        userMapper.insert(user);
    }

    @Test
    void createInterview_DB까지저장확인() throws Exception {
        InterviewRequest request = new InterviewRequest();
        request.setUserId("user-123");
        request.setTopic("spring");


        var mvcResult = mockMvc.perform(post("/api/interviews")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        InterviewResponse resp = objectMapper.readValue(json, InterviewResponse.class);
        String interviewId = resp.getInterviewId();

        var saved = interviewMapper.findById(interviewId);
        assertThat(saved).isNotNull();
        assertThat(saved.userId()).isEqualTo("user-123");
        assertThat(saved.topic()).isEqualTo("spring");
    }
}