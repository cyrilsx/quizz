package com.quizzplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Configuration
public class RateLimitConfig {

    @Bean
    public ConcurrentHashMap<String, LoginAttemptTracker> loginAttemptCache() {
        return new ConcurrentHashMap<>();
    }

    public static class LoginAttemptTracker {
        public int attemptCount = 0;
        public long lastAttemptTime = System.currentTimeMillis();

        public boolean isRateLimited() {
            long currentTime = System.currentTimeMillis();
            // Reset counter if more than 1 minute has passed
            if (currentTime - lastAttemptTime > TimeUnit.MINUTES.toMillis(1)) {
                attemptCount = 0;
                lastAttemptTime = currentTime;
            }
            
            attemptCount++;
            lastAttemptTime = currentTime;
            
            return attemptCount > 5; // Allow 5 attempts per minute
        }
    }
}