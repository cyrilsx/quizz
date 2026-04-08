package com.quizzplatform.quizz;

import com.quizzplatform.util.QuizValidator;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
public class QuizzController {

    private final QuizService quizService;
    private final QuizValidator quizValidator;

    public QuizzController(QuizService quizService, QuizValidator quizValidator) {
        this.quizService = quizService;
        this.quizValidator = quizValidator;
    }

    @GetMapping
    public ResponseEntity<List<QuizResponse>> getAllQuizzes() {
        List<QuizEntity> quizzes = quizService.getAllPublicQuizzes();
        List<QuizResponse> responses = quizzes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id)
                .map(quiz -> ResponseEntity.ok(convertToResponse(quiz)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(@RequestBody com.quizzplatform.api.model.QuizRequest request) {
        try {
            // Validate and sanitize input
            quizValidator.validateAndSanitize(request);
            
            // In a real implementation, you would get the user ID from the JWT token
            Long userId = 1L; // Temporary for demo
            QuizEntity quiz = new QuizEntity();
            quiz.setTitle(request.getTitle());
            quiz.setDescription(request.getDescription());
            quiz.setPublic(request.getIsPublic());
            QuizEntity createdQuiz = quizService.createQuiz(quiz, userId);
            return ResponseEntity.ok(convertToResponse(createdQuiz));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizResponse> updateQuiz(@PathVariable Long id, @RequestBody QuizRequest request) {
        QuizEntity quiz = new QuizEntity();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setPublic(request.isPublic());
        QuizEntity updatedQuiz = quizService.updateQuiz(id, quiz);
        return ResponseEntity.ok(convertToResponse(updatedQuiz));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<ShareResponse> generateShareToken(@PathVariable Long id) {
        String token = quizService.generateShareToken(id);
        return ResponseEntity.ok(new ShareResponse(token));
    }

    private QuizResponse convertToResponse(QuizEntity quiz) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setDescription(quiz.getDescription());
        response.setPublic(quiz.isPublic());
        response.setCreatedAt(quiz.getCreatedAt());
        if (quiz.getUser() != null) {
            response.setUserId(quiz.getUser().getId());
            response.setUsername(quiz.getUser().getUsername());
        }
        return response;
    }

    public static class QuizRequest {
        private String title;
        private String description;
        private boolean isPublic;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isPublic() {
            return isPublic;
        }

        public void setPublic(boolean isPublic) {
            this.isPublic = isPublic;
        }
    }

    public static class QuizResponse {
        private Long id;
        private String title;
        private String description;
        private boolean isPublic;
        private Long userId;
        private String username;
        private LocalDateTime createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isPublic() {
            return isPublic;
        }

        public void setPublic(boolean isPublic) {
            this.isPublic = isPublic;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class ShareResponse {
        private String shareToken;

        public ShareResponse(String shareToken) {
            this.shareToken = shareToken;
        }

        public String getShareToken() {
            return shareToken;
        }

        public void setShareToken(String shareToken) {
            this.shareToken = shareToken;
        }
    }
}