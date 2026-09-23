package com.quizzplatform.quizz;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShareRepository extends JpaRepository<ShareEntity, Long> {

    Optional<ShareEntity> findByShareToken(String shareToken);

    void deleteByQuizId(Long quizId);
}
