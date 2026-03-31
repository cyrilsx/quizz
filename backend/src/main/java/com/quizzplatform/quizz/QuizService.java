package com.quizzplatform.quizz;

import com.quizzplatform.auth.UserEntity;
import com.quizzplatform.auth.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    public List<QuizEntity> getAllPublicQuizzes() {
        return quizRepository.findByIsPublicTrue();
    }

    public Optional<QuizEntity> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    public List<QuizEntity> getQuizzesByUserId(Long userId) {
        return quizRepository.findByUserId(userId);
    }

    @Transactional
    public QuizEntity createQuiz(QuizEntity quiz, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        quiz.setUser(user);
        return quizRepository.save(quiz);
    }

    @Transactional
    public QuizEntity updateQuiz(Long id, QuizEntity quizDetails) {
        QuizEntity quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setTitle(quizDetails.getTitle());
        quiz.setDescription(quizDetails.getDescription());
        quiz.setPublic(quizDetails.isPublic());
        return quizRepository.save(quiz);
    }

    @Transactional
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    public String generateShareToken(Long quizId) {
        // Generate a unique share token
        String token = UUID.randomUUID().toString();
        // In a real implementation, you would save this to the shares table
        return token;
    }
}