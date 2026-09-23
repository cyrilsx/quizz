package com.quizzplatform.integration;

import com.quizzplatform.QuizzBackendApplication;
import com.quizzplatform.auth.UserEntity;
import com.quizzplatform.auth.UserRepository;
import com.quizzplatform.quizz.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = QuizzBackendApplication.class)
class QuizIntegrationTest extends PostgresTestContainer {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private TempSessionRepository tempSessionRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        // Clean up database
        quizRepository.deleteAll();
        userRepository.deleteAll();
        tempSessionRepository.deleteAll();

        // Create test user
        testUser = new UserEntity();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMy.M5c6S3b5J8JqLJ5J8JqLJ5J8JqLJ5J8"); // BCrypt encoded "password"
        userRepository.save(testUser);
    }

    @Test
    void testFullQuizWorkflow() {
        // Create a quiz with questions and answers using cascade
        QuizEntity quiz = new QuizEntity();
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Integration test quiz");
        quiz.setPublic(true);
        quiz.setUser(testUser);
        
        // Create question with answers
        QuestionEntity question1 = new QuestionEntity();
        question1.setQuestionText("What is 2+2?");
        question1.setQuiz(quiz);
        
        AnswerEntity answer1 = new AnswerEntity();
        answer1.setAnswerText("4");
        answer1.setCorrect(true);
        answer1.setQuestion(question1);
        
        AnswerEntity answer2 = new AnswerEntity();
        answer2.setAnswerText("5");
        answer2.setCorrect(false);
        answer2.setQuestion(question1);
        
        question1.setAnswers(List.of(answer1, answer2));
        quiz.setQuestions(List.of(question1));
        
        QuizEntity savedQuiz = quizRepository.save(quiz);
        assertNotNull(savedQuiz.getId());

        // Verify data was saved with cascade
        QuizEntity foundQuiz = quizRepository.findById(savedQuiz.getId()).orElse(null);
        assertNotNull(foundQuiz);
        assertEquals("Test Quiz", foundQuiz.getTitle());
        assertNotNull(foundQuiz.getQuestions());
        assertEquals(1, foundQuiz.getQuestions().size());
        assertEquals("What is 2+2?", foundQuiz.getQuestions().get(0).getQuestionText());
        assertEquals(2, foundQuiz.getQuestions().get(0).getAnswers().size());

        // Test quiz service methods
        List<QuizEntity> publicQuizzes = quizService.getAllPublicQuizzes();
        assertEquals(1, publicQuizzes.size());

        // Test share token generation
        String shareToken = quizService.generateShareToken(savedQuiz.getId(), testUser.getId());
        assertNotNull(shareToken);
        assertTrue(shareToken.length() > 0);
        // Share token resolves back to the quiz
        assertTrue(quizService.getQuizIdByShareToken(shareToken).isPresent());
        assertEquals(savedQuiz.getId(), quizService.getQuizIdByShareToken(shareToken).get());

        // Test quiz update
        foundQuiz.setTitle("Updated Test Quiz");
        QuizEntity updatedQuiz = quizService.updateQuiz(foundQuiz.getId(), foundQuiz, testUser.getId());
        assertEquals("Updated Test Quiz", updatedQuiz.getTitle());

        // Test quiz deletion (cascade should delete questions and answers too)
        quizService.deleteQuiz(foundQuiz.getId(), testUser.getId());
        assertFalse(quizRepository.existsById(foundQuiz.getId()));
    }

    @Test
    void testQuizWithMultipleQuestions() {
        QuizEntity quiz = new QuizEntity();
        quiz.setTitle("Multi-Question Quiz");
        quiz.setDescription("Quiz with multiple questions");
        quiz.setPublic(false);
        quiz.setUser(testUser);
        
        // Create multiple questions with answers using cascade
        List<QuestionEntity> questions = new java.util.ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            QuestionEntity question = new QuestionEntity();
            question.setQuestionText("Question " + i);
            question.setQuiz(quiz);
            
            AnswerEntity correctAnswer = new AnswerEntity();
            correctAnswer.setAnswerText("Correct Answer " + i);
            correctAnswer.setCorrect(true);
            correctAnswer.setQuestion(question);
            
            AnswerEntity wrongAnswer = new AnswerEntity();
            wrongAnswer.setAnswerText("Wrong Answer " + i);
            wrongAnswer.setCorrect(false);
            wrongAnswer.setQuestion(question);
            
            question.setAnswers(List.of(correctAnswer, wrongAnswer));
            questions.add(question);
        }
        quiz.setQuestions(questions);
        
        QuizEntity savedQuiz = quizRepository.save(quiz);

        // Verify all questions were saved with cascade
        QuizEntity foundQuiz = quizRepository.findById(savedQuiz.getId()).orElse(null);
        assertNotNull(foundQuiz);
        assertNotNull(foundQuiz.getQuestions());
        assertEquals(3, foundQuiz.getQuestions().size());
        
        // Verify each question has 2 answers
        for (QuestionEntity question : foundQuiz.getQuestions()) {
            assertNotNull(question.getAnswers());
            assertEquals(2, question.getAnswers().size());
            assertTrue(question.getAnswers().stream().anyMatch(AnswerEntity::isCorrect));
            assertTrue(question.getAnswers().stream().anyMatch(a -> !a.isCorrect()));
        }
    }

    @Test
    void testTempSessionManagement() {
        TempSessionEntity session = new TempSessionEntity();
        session.setIpAddress("192.168.1.1");
        session.setQuizCount(0);
        session.setSessionToken("token");
        session.setCreatedAt(LocalDateTime.now());
        
        TempSessionEntity savedSession = tempSessionRepository.save(session);
        assertNotNull(savedSession.getId());

        // Test session update
        savedSession.setQuizCount(1);
        TempSessionEntity updatedSession = tempSessionRepository.save(savedSession);
        assertEquals(1, updatedSession.getQuizCount());

        // Test session retrieval
        TempSessionEntity foundSession = tempSessionRepository.findByIpAddress("192.168.1.1").orElse(null);
        assertNotNull(foundSession);
        assertEquals(1, foundSession.getQuizCount());
    }

}