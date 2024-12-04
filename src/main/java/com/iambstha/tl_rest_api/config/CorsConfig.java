package com.iambstha.tl_rest_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .exposedHeaders("xsrf-token", "Authorization", "Content-Disposition")
                .allowedHeaders("Content-Disposition", "X-Requested-With", "Content-Type", "Accept", "X-Auth-Token", "X-Csrf-Token", "Authorization", "Key")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
