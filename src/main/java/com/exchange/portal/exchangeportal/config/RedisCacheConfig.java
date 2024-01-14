package com.exchange.portal.exchangeportal.config;

import com.exchange.portal.exchangeportal.helper.RedisHelper;
import com.exchange.portal.exchangeportal.service.redis.RedisService;
import com.exchange.portal.exchangeportal.service.redis.impl.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.StringRedisTemplate;

@ComponentScan(
        basePackageClasses = {RedisServiceImpl.class}
)
@SpringBootConfiguration
public class RedisCacheConfig {
    @Value("${servicename}")
    private String serviceName;
    @Value("${redisname:#{null}}")
    private String redisName;

    public RedisCacheConfig() {
    }

    @Bean
    @ConditionalOnMissingBean({RedisService.class})
    public RedisService redisService(RedisHelper redisHelper, StringRedisTemplate redisTemplate) {
        String cacheKeyName = this.redisName != null ? this.redisName : this.serviceName;
        return new RedisServiceImpl(cacheKeyName, redisHelper, redisTemplate, redisTemplate);
    }
}
