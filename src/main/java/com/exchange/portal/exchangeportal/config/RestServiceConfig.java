package com.exchange.portal.exchangeportal.config;

import com.exchange.portal.exchangeportal.common.rest.CommonRestServiceImpl;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(
        basePackageClasses = {
                CommonRestServiceImpl.class
        })
@SpringBootConfiguration
public class RestServiceConfig {
}

