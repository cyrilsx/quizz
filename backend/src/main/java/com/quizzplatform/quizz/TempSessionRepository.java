package com.quizzplatform.quizz;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TempSessionRepository extends JpaRepository<TempSessionEntity, Long> {
    Optional<TempSessionEntity> findBySessionToken(String sessionToken);
    Optional<TempSessionEntity> findByIpAddress(String ipAddress);
}