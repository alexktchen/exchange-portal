package com.exchange.portal.exchangeportal;


import com.exchange.portal.exchangeportal.config.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@Import({CommonConfig.class,
        RedisCacheConfig.class,
        TaskPoolConfig.class,
        MybatisConfig.class,
        MvcConfig.class})
@EnableScheduling
public class ExchangePortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangePortalApplication.class, args);
    }
}
