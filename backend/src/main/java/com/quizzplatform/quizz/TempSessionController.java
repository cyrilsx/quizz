package com.quizzplatform.quizz;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temp-sessions")
public class TempSessionController {

    private final TempSessionService tempSessionService;

    public TempSessionController(TempSessionService tempSessionService) {
        this.tempSessionService = tempSessionService;
    }

    @PostMapping("/init")
    public ResponseEntity<TempSessionResponse> initSession(@RequestParam String ipAddress) {
        TempSessionEntity session = tempSessionService.createOrUpdateSession(ipAddress);
        return ResponseEntity.ok(new TempSessionResponse(session.getSessionToken(), session.getQuizCount()));
    }

    @GetMapping("/check")
    public ResponseEntity<CanCreateQuizResponse> checkCanCreateQuiz(@RequestParam String ipAddress) {
        boolean canCreate = tempSessionService.canCreateQuiz(ipAddress);
        return ResponseEntity.ok(new CanCreateQuizResponse(canCreate));
    }

    public static class TempSessionResponse {
        private String sessionToken;
        private int quizCount;

        public TempSessionResponse(String sessionToken, int quizCount) {
            this.sessionToken = sessionToken;
            this.quizCount = quizCount;
        }

        public String getSessionToken() {
            return sessionToken;
        }

        public void setSessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
        }

        public int getQuizCount() {
            return quizCount;
        }

        public void setQuizCount(int quizCount) {
            this.quizCount = quizCount;
        }
    }

    public static class CanCreateQuizResponse {
        private boolean canCreate;

        public CanCreateQuizResponse(boolean canCreate) {
            this.canCreate = canCreate;
        }

        public boolean isCanCreate() {
            return canCreate;
        }

        public void setCanCreate(boolean canCreate) {
            this.canCreate = canCreate;
        }
    }
}