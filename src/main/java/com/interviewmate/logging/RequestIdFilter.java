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
    private static final String TOTAL_KEY = "total";
    private static final String DB_KEY = "dbElapsed";
    private static final String AI_KEY = "aiElapsed";

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RequestIdFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_KEY, requestId);

        logger.info("Filter approach – URI: {}", request.getRequestURI());

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);

        } finally {
            long totalElapsed = System.currentTimeMillis() - startTime;
            MDC.put(TOTAL_KEY, String.valueOf(totalElapsed));

            String dbElapsedValue = MDC.get(DB_KEY);
            String aiElapsedValue = MDC.get(AI_KEY);
            if (dbElapsedValue == null) {
                MDC.put(DB_KEY, "0");
            }
            if (aiElapsedValue == null) {
                MDC.put(AI_KEY, "0");
            }


            logger.info("Request completed– method={}, uri={}, status={}, req={}, total={}ms, db={}ms, ai={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    requestId,
                    MDC.get(TOTAL_KEY),
                    MDC.get(DB_KEY),
                    MDC.get(AI_KEY)
            );

            MDC.clear();
        }
    }
}