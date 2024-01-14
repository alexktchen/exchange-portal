package com.exchange.portal.exchangeportal.common.auth;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Mapping
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface UserAuth {

  boolean required() default false;


  boolean beEmpty() default false;
}
