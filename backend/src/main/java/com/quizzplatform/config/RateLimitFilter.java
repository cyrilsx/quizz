package com.quizzplatform.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitFilter implements Filter {

    private final ConcurrentHashMap<String, RateLimitConfig.LoginAttemptTracker> loginAttemptCache;

    public RateLimitFilter(ConcurrentHashMap<String, RateLimitConfig.LoginAttemptTracker> loginAttemptCache) {
        this.loginAttemptCache = loginAttemptCache;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Only apply rate limiting to login endpoints
        if (!httpRequest.getRequestURI().contains("/api/auth/login")) {
            chain.doFilter(request, response);
            return;
        }
        
        String clientIp = getClientIp(httpRequest);
        RateLimitConfig.LoginAttemptTracker tracker = loginAttemptCache.computeIfAbsent(clientIp, 
                ip -> new RateLimitConfig.LoginAttemptTracker());
        
        if (tracker.isRateLimited()) {
            // Request is rejected
            long waitTime = TimeUnit.MINUTES.toSeconds(1) - 
                    TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - tracker.lastAttemptTime);
            httpResponse.setStatus(429); // Too Many Requests
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\":\"Too many login attempts. Please try again in " + 
                    Math.max(waitTime, 0) + " seconds.\"}");
        } else {
            // Request is allowed
            httpResponse.addHeader("X-Rate-Limit-Remaining", String.valueOf(5 - tracker.attemptCount));
            chain.doFilter(request, response);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // Handle multiple IPs in X-Forwarded-For
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        return ipAddress;
    }
}