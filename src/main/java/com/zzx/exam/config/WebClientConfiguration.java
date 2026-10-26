package com.zzx.exam.config;

import com.zzx.exam.config.properties.KimiProperties;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(KimiProperties.class)
public class WebClientConfiguration {
    
    @Autowired
    private KimiProperties kimiProperties;

    @Bean
    public WebClient webClient() {
        WebClient webClient = WebClient.builder().
                baseUrl(kimiProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + kimiProperties.getApiKey())
                .build();
        return webClient;
    }
}