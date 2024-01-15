package com.example.shopapp.components;

import com.example.shopapp.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LocalizationUtils {
    private final MessageSource messageSource;
    private final LocaleResolver localResolver;

    public String getLocalizeMessage(String messageKey,Object ...params) {
        HttpServletRequest request = WebUtils.getCurrentRequest();
        Locale locale = localResolver.resolveLocale(request);
        System.out.println(messageKey);
        System.out.println(messageSource.getMessage(messageKey ,params,  locale));
        return messageSource.getMessage(messageKey ,params,  locale);
    }
}
