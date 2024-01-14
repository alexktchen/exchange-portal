package com.exchange.portal.exchangeportal.config;

import com.exchange.portal.exchangeportal.common.RequestPlatform;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestAttribute;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;


@Component
@EnableSwagger2
public class Swagger2Config {

    @Value("${service.application.base.path}")
    private String basePath;

    @Value("${swagger.title}")
    private String title;

    @Value("${swagger.description}")
    private String description;

    @Bean
    public Docket createRestApi() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar.name("Authorization").description("token").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        ParameterBuilder localePar = new ParameterBuilder();
        localePar.name(HttpHeaders.ACCEPT_LANGUAGE).modelRef(new ModelRef("string")).parameterType("header").defaultValue("zh-cn").build();
        List<Parameter> pars = new ArrayList<>(ImmutableList.of(tokenPar.build(), localePar.build()));
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(RequestAttribute.class, RequestPlatform.class)
                .globalOperationParameters(pars)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePath))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .build();
    }

}
