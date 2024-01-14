package com.exchange.portal.exchangeportal.common.vo;

import com.exchange.portal.exchangeportal.common.jackson.BaseJsonView;
import com.exchange.portal.exchangeportal.common.constant.CommonErrorTypes;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@JsonView(BaseJsonView.class)
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int SUCCESS_CODE = CommonErrorTypes.SUCCESS.getCode();
    public static final String SUCCESS_MSG = CommonErrorTypes.SUCCESS.getMessage();
    public static final int FAILED_CODE = 1;
    public static final String FAILED_MSG = "failed";
    public static final int SUCCESS = 200;
    public static final int BAD_REQUEST = 400;
    public static final int ENCRYPT_ERR = 500;
    public static final int DECRYPT_ERR = 501;
    public static final int DECRYPT__METHOD_ERR = 502;

    private String msg = SUCCESS_MSG;
    private int status = SUCCESS_CODE;
    private T data;

    public Result() {
        super();
    }

    public Result(T data, ErrorTypeVO errorType){
        this(data, errorType.getMessage(), errorType.getCode());
    }

    public Result(ErrorTypeVO errorType){
        super();
        this.msg = errorType.getMessage();
        this.status = errorType.getCode();
    }

    public Result(T data, String msg, int code) {
        super();
        this.data = data;
        this.status = code;
        this.msg = msg;
    }

    public Result(String msg, int code) {
        super();
        this.status = code;
        this.msg = msg;
    }

    public Result(T data) {
        super();
        this.data = data;
    }

    public Long getTime() {
        return Instant.now().toEpochMilli();
    }

}
