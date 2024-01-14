package com.exchange.portal.exchangeportal.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(basePackages = "com.exchange.portal.exchangeportal.common.exception")
@SpringBootConfiguration
public class ExceptionConfig {

    @Bean
    public ErrorProperties errorProperties(){
        return new ErrorProperties();
    }

    @Bean
    public ErrorAttributes errorAttributes(){
        return new DefaultErrorAttributes();
    }

}
