package com.quizzplatform.translation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class TranslationService {

    private final MessageSource messageSource;

    public TranslationService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getTranslation(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, key, locale);
    }

    public String getTranslation(String key, Locale locale) {
        return messageSource.getMessage(key, null, key, locale);
    }

    public String getTranslation(String key, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }

    public String getTranslation(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, key, locale);
    }
}