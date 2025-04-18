package com.github.cortex.configuration;

import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}