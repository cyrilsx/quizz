package com.quizzplatform.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Content Security Policy
        httpResponse.setHeader("Content-Security-Policy", "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data:; " +
                "font-src 'self'; " +
                "connect-src 'self' http://localhost:4200 https://api.mistral.ai; " +
                "frame-ancestors 'self'");
        
        // XSS Protection
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        // X-Content-Type-Options
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        
        // X-Frame-Options
        httpResponse.setHeader("X-Frame-Options", "DENY");
        
        // Referrer Policy
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Permissions Policy
        httpResponse.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
        
        // Cache Control for sensitive data
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        httpResponse.setHeader("Pragma", "no-cache");
        
        chain.doFilter(request, response);
    }
}