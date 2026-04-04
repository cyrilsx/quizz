package com.quizzplatform.quizz;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
    List<QuizEntity> findByIsPublicTrue();
    List<QuizEntity> findByUserId(Long userId);
    
    @EntityGraph(attributePaths = {"questions", "questions.answers"})
    Optional<QuizEntity> findById(Long id);
}