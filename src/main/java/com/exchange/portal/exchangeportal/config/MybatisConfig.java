package com.exchange.portal.exchangeportal.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages={"com.exchange.portal.exchangeportal.common.db.mapper"})
public class MybatisConfig {

}

