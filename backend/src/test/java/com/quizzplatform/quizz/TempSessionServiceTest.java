package com.quizzplatform.quizz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TempSessionServiceTest {

    @Mock
    private TempSessionRepository tempSessionRepository;

    @InjectMocks
    private TempSessionService tempSessionService;

    private TempSessionEntity testSession;

    @BeforeEach
    void setUp() {
        testSession = new TempSessionEntity();
        testSession.setId(1L);
        testSession.setSessionToken("test-token");
        testSession.setIpAddress("192.168.1.1");
        testSession.setQuizCount(3);
    }

    @Test
    void createOrUpdateSession_NewSession() {
        TempSessionEntity newSession = new TempSessionEntity();
        newSession.setQuizCount(1); // This should be set by the service
        newSession.setSessionToken("new-token");
        
        when(tempSessionRepository.findByIpAddress("192.168.1.1")).thenReturn(Optional.empty());
        when(tempSessionRepository.save(any(TempSessionEntity.class))).thenAnswer(invocation -> {
            TempSessionEntity sessionToSave = invocation.getArgument(0);
            sessionToSave.setId(1L);
            return sessionToSave;
        });

        TempSessionEntity result = tempSessionService.createOrUpdateSession("192.168.1.1");

        assertNotNull(result);
        assertEquals(1, result.getQuizCount());
        assertNotNull(result.getSessionToken());
        verify(tempSessionRepository, times(1)).save(any(TempSessionEntity.class));
    }

    @Test
    void createOrUpdateSession_ExistingSession() {
        when(tempSessionRepository.findByIpAddress("192.168.1.1")).thenReturn(Optional.of(testSession));
        when(tempSessionRepository.save(any(TempSessionEntity.class))).thenReturn(testSession);

        TempSessionEntity result = tempSessionService.createOrUpdateSession("192.168.1.1");

        assertNotNull(result);
        assertEquals(4, result.getQuizCount()); // Should increment from 3 to 4
        verify(tempSessionRepository, times(1)).save(any(TempSessionEntity.class));
    }

    @Test
    void createOrUpdateSession_MaxLimitReached() {
        testSession.setQuizCount(5); // Set to max limit
        when(tempSessionRepository.findByIpAddress("192.168.1.1")).thenReturn(Optional.of(testSession));

        assertThrows(RuntimeException.class, () -> {
            tempSessionService.createOrUpdateSession("192.168.1.1");
        });
    }

    @Test
    void canCreateQuiz_UnderLimit() {
        testSession.setQuizCount(3);
        when(tempSessionRepository.findByIpAddress("192.168.1.1")).thenReturn(Optional.of(testSession));

        boolean result = tempSessionService.canCreateQuiz("192.168.1.1");

        assertTrue(result);
    }

    @Test
    void canCreateQuiz_AtLimit() {
        testSession.setQuizCount(5);
        when(tempSessionRepository.findByIpAddress("192.168.1.1")).thenReturn(Optional.of(testSession));

        boolean result = tempSessionService.canCreateQuiz("192.168.1.1");

        assertFalse(result);
    }

    @Test
    void canCreateQuiz_NoSession() {
        when(tempSessionRepository.findByIpAddress("192.168.1.1")).thenReturn(Optional.empty());

        boolean result = tempSessionService.canCreateQuiz("192.168.1.1");

        assertTrue(result);
    }

    @Test
    void getSessionByToken_Success() {
        when(tempSessionRepository.findBySessionToken("test-token")).thenReturn(Optional.of(testSession));

        TempSessionEntity result = tempSessionService.getSessionByToken("test-token");

        assertNotNull(result);
        assertEquals("test-token", result.getSessionToken());
    }

    @Test
    void getSessionByToken_NotFound() {
        when(tempSessionRepository.findBySessionToken("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            tempSessionService.getSessionByToken("nonexistent");
        });
    }
}