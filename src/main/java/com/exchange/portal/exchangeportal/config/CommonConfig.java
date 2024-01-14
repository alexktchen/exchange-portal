package com.exchange.portal.exchangeportal.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootConfiguration
public class CommonConfig {
    @Bean
    public RestTemplate restTemplateCommon() {
        return new RestTemplateBuilder().build();
    }
}
