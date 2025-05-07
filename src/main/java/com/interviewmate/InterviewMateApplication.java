package com.interviewmate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@MapperScan(basePackages = "com.interviewmate.interview.repository")
public class InterviewMateApplication {
	public static void main(String[] args) {
		SpringApplication.run(InterviewMateApplication.class, args);
	}
}