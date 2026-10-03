package com.quizzplatform.quizz;

import com.quizzplatform.auth.UserEntity;
import com.quizzplatform.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShareRepository shareRepository;

    @InjectMocks
    private QuizService quizService;

    private QuizEntity testQuiz;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testQuiz = new QuizEntity();
        testQuiz.setId(1L);
        testQuiz.setTitle("Test Quiz");
        testQuiz.setDescription("Test Description");
        testQuiz.setPublic(true);
        testQuiz.setUser(testUser);
    }

    @Test
    void getAllPublicQuizzes_Success() {
        when(quizRepository.findByIsPublicTrue()).thenReturn(Arrays.asList(testQuiz));
        List<QuizEntity> quizzes = quizService.getAllPublicQuizzes();
        assertNotNull(quizzes);
        assertEquals(1, quizzes.size());
        assertEquals("Test Quiz", quizzes.get(0).getTitle());
    }

    @Test
    void getAllPublicQuizzes_Empty() {
        when(quizRepository.findByIsPublicTrue()).thenReturn(Arrays.asList());
        List<QuizEntity> quizzes = quizService.getAllPublicQuizzes();
        assertNotNull(quizzes);
        assertTrue(quizzes.isEmpty());
    }

    @Test
    void getQuizById_Success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        Optional<QuizEntity> quiz = quizService.getQuizById(1L);
        assertTrue(quiz.isPresent());
        assertEquals("Test Quiz", quiz.get().getTitle());
    }

    @Test
    void getQuizById_NotFound() {
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<QuizEntity> quiz = quizService.getQuizById(999L);
        assertFalse(quiz.isPresent());
    }

    @Test
    void getQuizzesByUserId_Success() {
        when(quizRepository.findByUserId(1L)).thenReturn(Arrays.asList(testQuiz));
        List<QuizEntity> quizzes = quizService.getQuizzesByUserId(1L);
        assertNotNull(quizzes);
        assertEquals(1, quizzes.size());
        assertEquals("Test Quiz", quizzes.get(0).getTitle());
    }

    @Test
    void createQuiz_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(quizRepository.save(any(QuizEntity.class))).thenAnswer(invocation -> {
            QuizEntity quizToSave = invocation.getArgument(0);
            quizToSave.setId(1L); // Simulate database assigning ID
            return quizToSave;
        });
        QuizEntity newQuiz = new QuizEntity();
        newQuiz.setTitle("New Quiz");
        newQuiz.setDescription("New Description");
        newQuiz.setPublic(false);
        QuizEntity createdQuiz = quizService.createQuiz(newQuiz, 1L);
        assertNotNull(createdQuiz);
        assertEquals("New Quiz", createdQuiz.getTitle());
        assertEquals(testUser, createdQuiz.getUser());
        verify(quizRepository, times(1)).save(any(QuizEntity.class));
    }

    @Test
    void createQuiz_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        QuizEntity newQuiz = new QuizEntity();
        newQuiz.setTitle("New Quiz");
        assertThrows(RuntimeException.class, () -> {
            quizService.createQuiz(newQuiz, 999L);
        });
    }

    @Test
    void updateQuiz_Success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizRepository.save(any(QuizEntity.class))).thenReturn(testQuiz);

        QuizEntity updatedQuiz = new QuizEntity();
        updatedQuiz.setTitle("Updated Quiz");
        updatedQuiz.setDescription("Updated Description");
        updatedQuiz.setPublic(false);

        QuizEntity result = quizService.updateQuiz(1L, updatedQuiz, 1L);

        assertNotNull(result);
        assertEquals("Updated Quiz", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.isPublic());
        verify(quizRepository, times(1)).save(any(QuizEntity.class));
    }

    @Test
    void updateQuiz_NotOwned() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        QuizEntity updatedQuiz = new QuizEntity();
        updatedQuiz.setTitle("Updated Quiz");

        assertThrows(SecurityException.class, () -> {
            quizService.updateQuiz(1L, updatedQuiz, 999L);
        });
        verify(quizRepository, never()).save(any(QuizEntity.class));
    }

    @Test
    void updateQuiz_NotFound() {
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());

        QuizEntity updatedQuiz = new QuizEntity();
        updatedQuiz.setTitle("Updated Quiz");

        assertThrows(RuntimeException.class, () -> {
            quizService.updateQuiz(999L, updatedQuiz, 1L);
        });
    }

    @Test
    void deleteQuiz_Success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        assertDoesNotThrow(() -> {
            quizService.deleteQuiz(1L, 1L);
        });

        verify(quizRepository, times(1)).delete(testQuiz);
    }

    @Test
    void deleteQuiz_NotOwned() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        assertThrows(SecurityException.class, () -> {
            quizService.deleteQuiz(1L, 999L);
        });

        verify(quizRepository, never()).delete(any(QuizEntity.class));
    }

    @Test
    void generateShareToken_Success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(shareRepository.save(any(ShareEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = quizService.generateShareToken(1L, 1L);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        // UUID format validation
        assertTrue(token.contains("-"));
        verify(shareRepository, times(1)).save(any(ShareEntity.class));
    }

    @Test
    void generateShareToken_NotOwned() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        assertThrows(SecurityException.class, () -> {
            quizService.generateShareToken(1L, 999L);
        });
        verify(shareRepository, never()).save(any(ShareEntity.class));
    }

    @Test
    void generateShareToken_AnonymousQuiz() {
        QuizEntity anonymousQuiz = new QuizEntity();
        anonymousQuiz.setId(2L);
        anonymousQuiz.setTitle("Anonymous Quiz");
        when(quizRepository.findById(2L)).thenReturn(Optional.of(anonymousQuiz));
        when(shareRepository.save(any(ShareEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = quizService.generateShareToken(2L, 1L);

        assertNotNull(token);
        verify(shareRepository, times(1)).save(any(ShareEntity.class));
    }

    @Test
    void generateShareToken_QuizNotFound() {
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            quizService.generateShareToken(999L, 1L);
        });
        verify(shareRepository, never()).save(any(ShareEntity.class));
    }

    @Test
    void getQuizIdByShareToken_Success() {
        ShareEntity share = new ShareEntity();
        share.setQuizId(1L);
        share.setShareToken("token-123");
        when(shareRepository.findByShareToken("token-123")).thenReturn(Optional.of(share));

        Optional<Long> quizId = quizService.getQuizIdByShareToken("token-123");

        assertTrue(quizId.isPresent());
        assertEquals(1L, quizId.get());
    }

    @Test
    void getQuizIdByShareToken_NotFound() {
        when(shareRepository.findByShareToken("missing")).thenReturn(Optional.empty());

        Optional<Long> quizId = quizService.getQuizIdByShareToken("missing");

        assertFalse(quizId.isPresent());
    }
}
