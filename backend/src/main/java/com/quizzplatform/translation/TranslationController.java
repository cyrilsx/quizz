package com.quizzplatform.translation;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/translations")
public class TranslationController {

    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @GetMapping
    public ResponseEntity<TranslationResponse> getTranslation(
            @RequestParam String key,
            @RequestParam(required = false) String lang) {
        
        Locale locale = lang != null ? Locale.forLanguageTag(lang) : LocaleContextHolder.getLocale();
        String translation = translationService.getTranslation(key, locale);
        return ResponseEntity.ok(new TranslationResponse(key, translation, locale.toString()));
    }

    public static class TranslationResponse {
        private String key;
        private String translation;
        private String locale;

        public TranslationResponse(String key, String translation, String locale) {
            this.key = key;
            this.translation = translation;
            this.locale = locale;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getTranslation() {
            return translation;
        }

        public void setTranslation(String translation) {
            this.translation = translation;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }
    }
}