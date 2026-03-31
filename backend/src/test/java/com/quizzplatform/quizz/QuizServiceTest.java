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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

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

        QuizEntity result = quizService.updateQuiz(1L, updatedQuiz);

        assertNotNull(result);
        assertEquals("Updated Quiz", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.isPublic());
        verify(quizRepository, times(1)).save(any(QuizEntity.class));
    }

    @Test
    void updateQuiz_NotFound() {
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());

        QuizEntity updatedQuiz = new QuizEntity();
        updatedQuiz.setTitle("Updated Quiz");

        assertThrows(RuntimeException.class, () -> {
            quizService.updateQuiz(999L, updatedQuiz);
        });
    }

    @Test
    void deleteQuiz_Success() {
        doNothing().when(quizRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            quizService.deleteQuiz(1L);
        });

        verify(quizRepository, times(1)).deleteById(1L);
    }

    @Test
    void generateShareToken_Success() {
        String token = quizService.generateShareToken(1L);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        // UUID format validation
        assertTrue(token.contains("-"));
    }
}