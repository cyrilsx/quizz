package com.quizzplatform.quizz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class TempSessionService {

    private final TempSessionRepository tempSessionRepository;
    private static final int MAX_QUIZ_COUNT = 5;

    public TempSessionService(TempSessionRepository tempSessionRepository) {
        this.tempSessionRepository = tempSessionRepository;
    }

    @Transactional
    public TempSessionEntity createOrUpdateSession(String ipAddress) {
        Optional<TempSessionEntity> existingSession = tempSessionRepository.findByIpAddress(ipAddress);
        
        if (existingSession.isPresent()) {
            TempSessionEntity session = existingSession.get();
            if (session.getQuizCount() >= MAX_QUIZ_COUNT) {
                throw new RuntimeException("Maximum quiz limit reached for temporary sessions");
            }
            session.setQuizCount(session.getQuizCount() + 1);
            return tempSessionRepository.save(session);
        } else {
            TempSessionEntity newSession = new TempSessionEntity();
            newSession.setSessionToken(UUID.randomUUID().toString());
            newSession.setIpAddress(ipAddress);
            newSession.setQuizCount(1);
            return tempSessionRepository.save(newSession);
        }
    }

    public boolean canCreateQuiz(String ipAddress) {
        return tempSessionRepository.findByIpAddress(ipAddress)
                .map(session -> session.getQuizCount() < MAX_QUIZ_COUNT)
                .orElse(true);
    }

    public TempSessionEntity getSessionByToken(String sessionToken) {
        return tempSessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }
}