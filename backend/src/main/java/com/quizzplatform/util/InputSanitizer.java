package com.quizzplatform.util;

import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

    /**
     * Sanitize input to prevent XSS attacks
     * Removes potentially dangerous HTML/JS content while preserving basic formatting
     */
    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove script tags
        String sanitized = input.replaceAll("(?i)<script[^>]*>.*?</script>", "");
        
        // Remove on* event handlers
        sanitized = sanitized.replaceAll("(?i)on\\w+\\s*=", "");
        
        // Remove javascript: URLs
        sanitized = sanitized.replaceAll("(?i)javascript:", "");
        
        // Remove dangerous HTML tags
        String[] dangerousTags = {
            "script", "iframe", "frame", "frameset", "object", "applet", "embed", "link", "meta", "style"
        };
        
        for (String tag : dangerousTags) {
            sanitized = sanitized.replaceAll("(?i)<" + tag + "[^>]*>", "");
            sanitized = sanitized.replaceAll("(?i)</" + tag + "\\s*>", "");
        }
        
        // Trim and return
        return sanitized.trim();
    }

    /**
     * Validate that input doesn't contain dangerous patterns
     */
    public boolean isValidInput(String input) {
        if (input == null) {
            return true;
        }
        
        // Check for script tags
        if (input.matches("(?i).*<script[^>]*>.*?</script>.*")) {
            return false;
        }
        
        // Check for event handlers
        if (input.matches("(?i).*on\\w+\\s*=[\"'].+[\"'].*")) {
            return false;
        }
        
        // Check for javascript: URLs
        if (input.matches("(?i).*javascript:.*")) {
            return false;
        }
        
        return true;
    }
}