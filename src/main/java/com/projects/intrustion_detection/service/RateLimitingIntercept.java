package com.projects.intrustion_detection.service;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class RateLimitingIntercept implements HandlerInterceptor {
    private final RateLimitService rateLimitService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String apiKey = request.getHeader("X-Forwarded-For");
        String key = (apiKey != null) ? apiKey : request.getRemoteAddr();
        Bucket bucket = rateLimitService.resolveBucket(key);
        System.out.println("Available tokens for key: " + key + "is: " + bucket.getAvailableTokens());

        if (bucket.tryConsume(1)) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            return true;
        }
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("""
                    { "error": "RATE_LIMIT_EXCEEDED" }
                    """);

            return false;



    }
}