package com.quizzplatform.util;

import com.quizzplatform.api.model.QuizRequest;
import org.springframework.stereotype.Component;

@Component
public class QuizValidator {

    private final InputSanitizer inputSanitizer;

    public QuizValidator(InputSanitizer inputSanitizer) {
        this.inputSanitizer = inputSanitizer;
    }

    /**
     * Validate and sanitize quiz request
     */
    public void validateAndSanitize(QuizRequest quizRequest) {
        if (quizRequest == null) {
            throw new IllegalArgumentException("Quiz request cannot be null");
        }
        
        // Validate title
        if (quizRequest.getTitle() == null || quizRequest.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz title is required");
        }
        
        if (quizRequest.getTitle().length() > 100) {
            throw new IllegalArgumentException("Quiz title cannot exceed 100 characters");
        }
        
        // Sanitize title and description
        quizRequest.setTitle(inputSanitizer.sanitize(quizRequest.getTitle()));
        if (quizRequest.getDescription() != null) {
            quizRequest.setDescription(inputSanitizer.sanitize(quizRequest.getDescription()));
        }
    }
}