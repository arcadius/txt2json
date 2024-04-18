package com.abobos.springboot.demo.txt2json.config;

import java.time.Duration;

import com.abobos.springboot.demo.txt2json.interceptor.AccessLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ServiceConfig implements WebMvcConfigurer {

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private AccessLogger accessLogger;

    @Bean
    RestOperations restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(7))
                .setReadTimeout(Duration.ofSeconds(7))
                .rootUri(appConfig.getGeoIpApiBaseUrl())
                .build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLogger);
    }
}
