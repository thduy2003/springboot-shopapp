package com.example.shopapp.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Arrays;

@Configuration
public class LanguageConfig {
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n.messages"); // tên cơ sở của tệp tài liệu ngôn ngữ
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
