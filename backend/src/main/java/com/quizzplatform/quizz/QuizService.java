package com.quizzplatform.quizz;

import com.quizzplatform.auth.UserEntity;
import com.quizzplatform.auth.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final ShareRepository shareRepository;

    public QuizService(QuizRepository quizRepository, UserRepository userRepository, ShareRepository shareRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.shareRepository = shareRepository;
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
    public QuizEntity createAnonymousQuiz(QuizEntity quiz) {
        return quizRepository.save(quiz);
    }

    @Transactional
    public QuizEntity updateQuiz(Long id, QuizEntity quizDetails, Long userId) {
        QuizEntity quiz = getOwnedQuiz(id, userId);
        quiz.setTitle(quizDetails.getTitle());
        quiz.setDescription(quizDetails.getDescription());
        quiz.setPublic(quizDetails.isPublic());
        return quizRepository.save(quiz);
    }

    @Transactional
    public void deleteQuiz(Long id, Long userId) {
        QuizEntity quiz = getOwnedQuiz(id, userId);
        shareRepository.deleteByQuizId(quiz.getId());
        quizRepository.delete(quiz);
    }

    private QuizEntity getOwnedQuiz(Long id, Long userId) {
        QuizEntity quiz = quizRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));
        if (quiz.getUser() == null || !quiz.getUser().getId().equals(userId)) {
            throw new SecurityException("User does not own this quiz");
        }
        return quiz;
    }

    @Transactional(readOnly = true)
    public Optional<QuizEntity> getAccessibleQuiz(Long id, String shareToken, Long userId) {
        return quizRepository.findById(id)
                .filter(quiz -> quiz.isPublic()
                        || (userId != null && quiz.getUser() != null && quiz.getUser().getId().equals(userId))
                        || (shareToken != null && getQuizIdByShareToken(shareToken).filter(id::equals).isPresent()));
    }

    @Transactional
    public String generateShareToken(Long quizId, Long userId) {
        QuizEntity quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));
        if (quiz.getUser() == null || !quiz.getUser().getId().equals(userId)) {
            throw new SecurityException("User does not own this quiz");
        }

        ShareEntity share = new ShareEntity();
        share.setQuizId(quiz.getId());
        share.setShareToken(UUID.randomUUID().toString());
        shareRepository.save(share);
        return share.getShareToken();
    }

    @Transactional(readOnly = true)
    public Optional<Long> getQuizIdByShareToken(String shareToken) {
        return shareRepository.findByShareToken(shareToken)
                .filter(share -> share.getExpiresAt() == null || share.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(ShareEntity::getQuizId);
    }
}
