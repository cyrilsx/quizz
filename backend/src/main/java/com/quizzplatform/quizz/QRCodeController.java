package com.quizzplatform.quizz;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcodes")
public class QRCodeController {

    private final QRCodeService qrCodeService;
    private final QuizService quizService;

    public QRCodeController(QRCodeService qrCodeService, QuizService quizService) {
        this.qrCodeService = qrCodeService;
        this.quizService = quizService;
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<QRCodeResponse> generateQuizQRCode(
            @PathVariable Long quizId,
            @RequestParam(defaultValue = "http://localhost:8080") String baseUrl) {
        
        String shareToken = quizService.generateShareToken(quizId);
        String qrCodeBase64 = qrCodeService.generateQuizShareQRCode(baseUrl, quizId, shareToken);
        
        return ResponseEntity.ok(new QRCodeResponse(qrCodeBase64, shareToken));
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