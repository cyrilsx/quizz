package com.quizzplatform.ia;

import com.quizzplatform.quizz.QuestionEntity;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ia")
public class IaController {

    private final IaService iaService;

    public IaController(IaService iaService) {
        this.iaService = iaService;
    }

    @PostMapping("/generate-questions")
    public ResponseEntity<List<QuestionResponse>> generateQuestions(
            @RequestBody QuestionGenerationRequest request) {
        
        List<QuestionEntity> questions = iaService.generateQuestions(request.getTopic(), request.getCount());
        List<QuestionResponse> responses = questions.stream()
                .map(this::convertToResponse)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    private QuestionResponse convertToResponse(QuestionEntity question) {
        QuestionResponse response = new QuestionResponse();
        response.setQuestionText(question.getQuestionText());
        
        if (question.getAnswers() != null) {
            response.setAnswers(question.getAnswers().stream()
                    .map(answer -> new AnswerResponse(answer.getAnswerText(), answer.isCorrect()))
                    .collect(Collectors.toList()));
        }
        
        return response;
    }

    public static class QuestionGenerationRequest {
        private String topic;
        private int count;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class QuestionResponse {
        private String questionText;
        private List<AnswerResponse> answers;

        public String getQuestionText() {
            return questionText;
        }

        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }

        public List<AnswerResponse> getAnswers() {
            return answers;
        }

        public void setAnswers(List<AnswerResponse> answers) {
            this.answers = answers;
        }
    }

    public static class AnswerResponse {
        private String answerText;
        private boolean isCorrect;

        public AnswerResponse(String answerText, boolean isCorrect) {
            this.answerText = answerText;
            this.isCorrect = isCorrect;
        }

        public String getAnswerText() {
            return answerText;
        }

        public void setAnswerText(String answerText) {
            this.answerText = answerText;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public void setCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
    }
}