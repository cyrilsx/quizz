package com.quizzplatform.translation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private TranslationService translationService;

    @Test
    void getTranslation_Success() {
        when(messageSource.getMessage(eq("test.key"), isNull(), anyString(), any(Locale.class)))
                .thenReturn("Test Translation");
        
        String result = translationService.getTranslation("test.key");
        assertEquals("Test Translation", result);
    }

    @Test
    void getTranslation_WithLocale() {
        when(messageSource.getMessage(eq("test.key"), isNull(), anyString(), eq(Locale.FRENCH)))
                .thenReturn("Test Translation FR");
        
        String result = translationService.getTranslation("test.key", Locale.FRENCH);
        assertEquals("Test Translation FR", result);
    }

    @Test
    void getTranslation_KeyNotFound_ReturnsKey() {
        when(messageSource.getMessage(eq("nonexistent.key"), isNull(), anyString(), any(Locale.class)))
                .thenReturn("nonexistent.key");

        String result = translationService.getTranslation("nonexistent.key");
        assertEquals("nonexistent.key", result);
    }


}