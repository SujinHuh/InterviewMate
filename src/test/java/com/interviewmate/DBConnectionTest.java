package com.interviewmate;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

class DBConnectionTest {

    @Test
    void directJdbcConnectionTest() throws Exception {
        String url = "jdbc:mysql://localhost:3306/test_db?serverTimezone=Asia/Seoul";
        String username = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            assertThat(conn).isNotNull();
            System.out.println("▶ JDBC Connection OK: " + conn);
        }
    }
}