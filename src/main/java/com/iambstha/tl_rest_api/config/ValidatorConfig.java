package com.iambstha.tl_rest_api.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.xml.validation.Validator;

@Configuration
@Lazy
public class ValidatorConfig {

    public HibernatePropertiesCustomizer customizer(final Validator validator) {
        return hibernateProperties ->
                hibernateProperties.put("jakarta.persistence.validation.factory", validator);
    }

}
