package com.exchange.portal.exchangeportal.common.constant;

import com.exchange.portal.exchangeportal.common.vo.ErrorTypeVO;
import org.springframework.http.HttpStatus;

public class CommonErrorTypes {
    public static final ErrorTypeVO SUCCESS = new ErrorTypeVO(HttpStatus.OK, 200, "success");
    public static final ErrorTypeVO INTERNAL_SERVER_ERROR = new ErrorTypeVO(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Internal server error");
    public static final ErrorTypeVO INVALID_PARAMETER = new ErrorTypeVO(HttpStatus.BAD_REQUEST, 400, "Invalid parameter {}, value {}");
    public static final ErrorTypeVO UNAUTHORIZED = new ErrorTypeVO(HttpStatus.UNAUTHORIZED, 401, "Unauthorized");
    public static final ErrorTypeVO INVALID_TOKEN = new ErrorTypeVO(HttpStatus.FORBIDDEN, 401, "invalid token");

}
