package com.quizzplatform.quizz;

import com.quizzplatform.auth.UserEntity;
import com.quizzplatform.auth.UserRepository;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcodes")
public class QRCodeController {

    private final QRCodeService qrCodeService;
    private final QuizService quizService;
    private final UserRepository userRepository;

    public QRCodeController(QRCodeService qrCodeService, QuizService quizService, UserRepository userRepository) {
        this.qrCodeService = qrCodeService;
        this.quizService = quizService;
        this.userRepository = userRepository;
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<QRCodeResponse> generateQuizQRCode(
            @PathVariable Long quizId,
            @RequestParam(defaultValue = "http://localhost:8080") String baseUrl,
            Authentication authentication) {

        Long userId = resolveUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            String shareToken = quizService.generateShareToken(quizId, userId);
            String qrCodeBase64 = qrCodeService.generateQuizShareQRCode(baseUrl, quizId, shareToken);

            return ResponseEntity.ok(new QRCodeResponse(qrCodeBase64, shareToken));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Long resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        String username = principal instanceof UserDetails userDetails
                ? userDetails.getUsername()
                : principal.toString();
        return userRepository.findByUsername(username)
                .map(UserEntity::getId)
                .orElse(null);
    }

    public static class QRCodeResponse {
        private String qrCodeImage;
        private String shareToken;

        public QRCodeResponse(String qrCodeImage, String shareToken) {
            this.qrCodeImage = qrCodeImage;
            this.shareToken = shareToken;
        }

        public String getQrCodeImage() {
            return qrCodeImage;
        }

        public void setQrCodeImage(String qrCodeImage) {
            this.qrCodeImage = qrCodeImage;
        }

        public String getShareToken() {
            return shareToken;
        }

        public void setShareToken(String shareToken) {
            this.shareToken = shareToken;
        }
    }
}
