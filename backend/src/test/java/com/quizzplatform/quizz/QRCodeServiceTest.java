package com.quizzplatform.quizz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QRCodeServiceTest {

    @InjectMocks
    private QRCodeService qrCodeService;

    @Test
    void generateQRCode_Success() throws Exception {
        String qrCode = qrCodeService.generateQRCode("https://example.com", 200, 200);
        assertNotNull(qrCode);
        assertTrue(qrCode.length() > 0);
        // The QR code service returns just the base64 data, not the full data URL
        assertTrue(qrCode.matches("^[A-Za-z0-9+/]+={0,2}$")); // Base64 pattern
    }

    @Test
    void generateQRCode_EmptyText() {
        // Empty text should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            qrCodeService.generateQRCode("", 100, 100);
        });
    }

    @Test
    void generateQRCode_DifferentSizes() {
        assertDoesNotThrow(() -> {
            String smallQR = qrCodeService.generateQRCode("test", 50, 50);
            String largeQR = qrCodeService.generateQRCode("test", 300, 300);
            
            assertNotNull(smallQR);
            assertNotNull(largeQR);
            assertTrue(smallQR.length() < largeQR.length()); // Larger QR should have more data
        });
    }

    @Test
    void generateQuizShareQRCode_Success() throws Exception {
        String qrCode = qrCodeService.generateQuizShareQRCode("https://quizplatform.com", 123L, "abc-def-ghi");
        assertNotNull(qrCode);
        assertTrue(qrCode.length() > 0);
        // The QR code service returns just the base64 data, not the full data URL
        assertTrue(qrCode.matches("^[A-Za-z0-9+/]+={0,2}$")); // Base64 pattern
    }

    @Test
    void generateQuizShareQRCode_ValidUrlFormat() {
        assertDoesNotThrow(() -> {
            String qrCode = qrCodeService.generateQuizShareQRCode("http://localhost:8080", 456L, "xyz-123-789");
            assertNotNull(qrCode);
            assertTrue(qrCode.length() > 0);
        });
    }

    @Test
    void generateQRCode_SpecialCharacters() {
        assertDoesNotThrow(() -> {
            String specialText = "https://example.com?param=value&other=test#fragment";
            String qrCode = qrCodeService.generateQRCode(specialText, 200, 200);
            assertNotNull(qrCode);
            assertTrue(qrCode.length() > 0);
        });
    }
}