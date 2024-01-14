package com.exchange.portal.exchangeportal.common.exception;

import com.exchange.portal.exchangeportal.common.constant.CommonErrorTypes;
import com.exchange.portal.exchangeportal.common.constant.exception.ApiException;
import com.exchange.portal.exchangeportal.common.vo.Result;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.NestedServletException;

import javax.servlet.ServletException;
import java.net.ConnectException;
import java.net.UnknownHostException;


@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity handleUnAuthorizedException(UnAuthorizedException e) {
        log.error(ExceptionUtils.getStackTrace(e));
        ApiException ae = ApiException.builder().errorType(CommonErrorTypes.UNAUTHORIZED).message(e.getMessage()).rootCause(e).build();
        return new ResponseEntity<Result>(ae.generateErrorResult(), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMsg = "{} {}";
        FieldError fieldError = e.getBindingResult().getFieldError();

        ApiException ae = ApiException.builder().errorType(CommonErrorTypes.INVALID_PARAMETER).message(errorMsg).arguments(fieldError.getField(), fieldError.getDefaultMessage()).rootCause(e).build();
        return new ResponseEntity<Result>(ae.generateErrorResult(), HttpStatus.OK);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ApiException ae = ApiException.builder().errorType(CommonErrorTypes.INVALID_PARAMETER).message(e.getMessage()).rootCause(e).build();
        return new ResponseEntity<Result>(ae.generateErrorResult(), HttpStatus.OK);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity handleHttpDefaultException(ServletException e) throws Exception {
        ApiException ae = ApiException.builder().errorType(CommonErrorTypes.INTERNAL_SERVER_ERROR).message(e.getMessage()).rootCause(e).build();
        return new ResponseEntity<Result>(ae.generateErrorResult(), ae.getErrorType().getHttpStatus());
    }

    @ExceptionHandler({NestedServletException.class})
    public ResponseEntity handleNestedException(NestedServletException e) throws Exception {
        if(e.getCause() instanceof ApiException) {
            ApiException ae = (ApiException) e.getCause();
            return new ResponseEntity<Result>(ae.generateErrorResult(), ae.getErrorType().getHttpStatus());
        }
        ApiException ae = ApiException.builder().errorType(CommonErrorTypes.INTERNAL_SERVER_ERROR).message(e.getMessage()).rootCause(e).build();
        return new ResponseEntity<Result>(ae.generateErrorResult(), ae.getErrorType().getHttpStatus());
    }
}
