package com.quizzplatform.quizz;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
    List<QuizEntity> findByIsPublicTrue();
    List<QuizEntity> findByUserId(Long userId);
}