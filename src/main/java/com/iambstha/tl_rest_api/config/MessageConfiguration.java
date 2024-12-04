package com.iambstha.tl_rest_api.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageConfiguration {

    @Bean("security")
    MessageSource securityMessageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setBasename("classpath:/messages/security/security");
        return messageSource;
    }

    @Bean("token")
    MessageSource tokenMessageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setBasename("classpath:/messages/token/token");
        return messageSource;
    }

    @Bean("user")
    MessageSource userMessageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setBasename("classpath:/messages/user/user");
        return messageSource;
    }

    @Bean("blog")
    MessageSource blogMessageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setBasename("classpath:/messages/blog/blog");
        return messageSource;
    }

    @Bean("comment")
    MessageSource commentMessageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setBasename("classpath:/messages/comment/comment");
        return messageSource;
    }

}
