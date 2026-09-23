package com.quizzplatform.quizz;

import com.quizzplatform.auth.UserEntity;
import com.quizzplatform.auth.UserRepository;
import com.quizzplatform.util.QuizValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
public class QuizzController {

    private final QuizService quizService;
    private final QuizValidator quizValidator;
    private final UserRepository userRepository;

    public QuizzController(QuizService quizService, QuizValidator quizValidator, UserRepository userRepository) {
        this.quizService = quizService;
        this.quizValidator = quizValidator;
        this.userRepository = userRepository;
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
    public ResponseEntity<QuizResponse> getQuizById(
            @PathVariable Long id,
            @RequestParam(required = false) String token,
            Authentication authentication) {
        return quizService.getAccessibleQuiz(id, token, resolveUserId(authentication))
                .map(quiz -> ResponseEntity.ok(convertToResponse(quiz)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(
            @RequestBody com.quizzplatform.api.model.QuizRequest request,
            Authentication authentication) {
        try {
            quizValidator.validateAndSanitize(request);

            QuizEntity quiz = new QuizEntity();
            quiz.setTitle(request.getTitle());
            quiz.setDescription(request.getDescription());
            quiz.setPublic(Boolean.TRUE.equals(request.getIsPublic()));

            Long userId = resolveUserId(authentication);
            QuizEntity createdQuiz = userId != null
                    ? quizService.createQuiz(quiz, userId)
                    : quizService.createAnonymousQuiz(quiz);
            return ResponseEntity.ok(convertToResponse(createdQuiz));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizResponse> updateQuiz(
            @PathVariable Long id,
            @RequestBody com.quizzplatform.api.model.QuizRequest request,
            Authentication authentication) {
        Long userId = resolveUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            quizValidator.validateAndSanitize(request);

            QuizEntity quiz = new QuizEntity();
            quiz.setTitle(request.getTitle());
            quiz.setDescription(request.getDescription());
            quiz.setPublic(Boolean.TRUE.equals(request.getIsPublic()));
            QuizEntity updatedQuiz = quizService.updateQuiz(id, quiz, userId);
            return ResponseEntity.ok(convertToResponse(updatedQuiz));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id, Authentication authentication) {
        Long userId = resolveUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            quizService.deleteQuiz(id, userId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<ShareResponse> generateShareToken(@PathVariable Long id, Authentication authentication) {
        Long userId = resolveUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            String token = quizService.generateShareToken(id, userId);
            return ResponseEntity.ok(new ShareResponse(token));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/shared/{token}")
    public ResponseEntity<QuizResponse> getQuizByShareToken(@PathVariable String token) {
        return quizService.getQuizIdByShareToken(token)
                .flatMap(quizService::getQuizById)
                .map(quiz -> ResponseEntity.ok(convertToResponse(quiz)))
                .orElse(ResponseEntity.notFound().build());
    }

    private Long resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        String username = principal instanceof UserDetails userDetails
                ? userDetails.getUsername()
                : principal.toString();
        return userRepository.findByUsername(username)
                .map(UserEntity::getId)
                .orElse(null);
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
