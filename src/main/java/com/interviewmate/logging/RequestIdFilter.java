package com.interviewmate.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RequestIdFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_KEY, requestId);

        logger.info("🔥 Filter 동작 확인 – URI: {}", request.getRequestURI());

        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            logger.info("요청 완료: method={}, uri={}, status={}, elapsed={}ms, requestId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    elapsed,
                    requestId
            );
            MDC.remove(REQUEST_ID_KEY);
        }
    }
}