package com.exchange.portal.exchangeportal.common.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class ErrorTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private HttpStatus httpStatus;
    private Integer code;
    private String message;
}
