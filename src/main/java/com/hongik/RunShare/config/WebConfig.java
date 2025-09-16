package com.hongik.RunShare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // http://localhost:3000 출처의 요청을 허용
                .allowedOrigins("http://localhost:3000")
                // 허용할 HTTP 메서드 (CRUD 및 OPTIONS)를 명시
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 요청에 포함될 수 있는 모든 헤더를 허용
                .allowedHeaders("*")
                // 자격 증명(쿠키, HTTP 인증 등)을 허용
                .allowCredentials(true);
    }
}
