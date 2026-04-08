package com.quizzplatform.quizz;

import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<TempSessionResponse> initSession(HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        TempSessionEntity session = tempSessionService.createOrUpdateSession(ipAddress);
        return ResponseEntity.ok(new TempSessionResponse(session.getSessionToken(), session.getQuizCount()));
    }

    @GetMapping("/check")
    public ResponseEntity<CanCreateQuizResponse> checkCanCreateQuiz(HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        boolean canCreate = tempSessionService.canCreateQuiz(ipAddress);
        return ResponseEntity.ok(new CanCreateQuizResponse(canCreate));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // Handle multiple IPs in X-Forwarded-For
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        return ipAddress;
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