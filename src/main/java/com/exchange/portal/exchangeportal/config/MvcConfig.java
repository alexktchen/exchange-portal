package com.exchange.portal.exchangeportal.config;

import com.exchange.portal.exchangeportal.core.auth.AuthUserInterceptor;
import com.exchange.portal.exchangeportal.service.auth.impl.AuthUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Order(1)
@Configuration
@ComponentScan(
        basePackageClasses = {
                AuthUserServiceImpl.class,
                AuthUserInterceptor.class,
        })
public class MvcConfig implements WebMvcConfigurer {
    private String[] SWAGGER_URL_EXCLUDE = new String[]{"/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**"};

    private AuthUserInterceptor authUserInterceptor;

    @Autowired
    public MvcConfig(AuthUserInterceptor authUserInterceptor) {
        this.authUserInterceptor = authUserInterceptor;

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // user auth interceptor
        registry.addInterceptor(authUserInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(SWAGGER_URL_EXCLUDE);
    }

}
