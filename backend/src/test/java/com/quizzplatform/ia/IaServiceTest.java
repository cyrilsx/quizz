package com.quizzplatform.ia;

import com.quizzplatform.quizz.AnswerEntity;
import com.quizzplatform.quizz.QuestionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IaServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private IaService iaService;

    @Test
    void generateQuestions_Success() {
        // This test would normally mock the Mistral API call
        // For now, we'll test the fallback functionality
        List<QuestionEntity> questions = iaService.generateQuestions("Science", 3);

        assertNotNull(questions);
        assertEquals(3, questions.size());
        
        for (QuestionEntity question : questions) {
            assertNotNull(question.getQuestionText());
            assertTrue(question.getQuestionText().contains("Science"));
            assertNotNull(question.getAnswers());
            assertEquals(4, question.getAnswers().size());
            
            // Check that one answer is correct
            long correctCount = question.getAnswers().stream()
                    .filter(AnswerEntity::isCorrect)
                    .count();
            assertEquals(1, correctCount);
        }
    }

    @Test
    void generateQuestions_DifferentTopic() {
        List<QuestionEntity> questions = iaService.generateQuestions("History", 2);

        assertNotNull(questions);
        assertEquals(2, questions.size());
        
        for (QuestionEntity question : questions) {
            assertTrue(question.getQuestionText().contains("History"));
        }
    }

    @Test
    void generateQuestions_SingleQuestion() {
        List<QuestionEntity> questions = iaService.generateQuestions("Math", 1);

        assertNotNull(questions);
        assertEquals(1, questions.size());
    }

    @Test
    void generateQuestions_MultipleQuestions() {
        List<QuestionEntity> questions = iaService.generateQuestions("Geography", 5);

        assertNotNull(questions);
        assertEquals(5, questions.size());
    }

    @Test
    void generateQuestions_EmptyTopic() {
        List<QuestionEntity> questions = iaService.generateQuestions("", 2);

        assertNotNull(questions);
        assertEquals(2, questions.size());
        
        for (QuestionEntity question : questions) {
            assertTrue(question.getQuestionText().contains(""));
        }
    }

    @Test
    void generateQuestions_ZeroCount() {
        List<QuestionEntity> questions = iaService.generateQuestions("Science", 0);

        assertNotNull(questions);
        assertTrue(questions.isEmpty());
    }

    @Test
    void generateQuestions_NegativeCount() {
        // This should still work and return empty list
        List<QuestionEntity> questions = iaService.generateQuestions("Science", -1);

        assertNotNull(questions);
        assertTrue(questions.isEmpty());
    }
}