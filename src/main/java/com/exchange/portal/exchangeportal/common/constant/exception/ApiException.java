package com.exchange.portal.exchangeportal.common.constant.exception;

import com.exchange.portal.exchangeportal.common.constant.CommonErrorTypes;
import com.exchange.portal.exchangeportal.common.vo.ErrorTypeVO;
import com.exchange.portal.exchangeportal.common.vo.Result;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiException extends RuntimeException {

    @Getter
    private ErrorTypeVO errorType;
    private String message;
    private Object[] arguments;
    private Throwable rootCause;

    @Override
    public String getMessage() {
        String msg = getPureMessage();

        log.error(msg);
        Optional.ofNullable(rootCause).ifPresent(e -> log.error(ExceptionUtils.getStackTrace(e)));

        return msg;
    }


    private String getPureMessage() {
        if (errorType == null) {
            errorType = CommonErrorTypes.INTERNAL_SERVER_ERROR;
        }

        if (Objects.equals(message, "")) {
            return errorType.getMessage();
        }

        return message;
    }


    @SuppressWarnings("unused")
    public static class ApiExceptionBuilder {
        private Object[] arguments;

        public ApiExceptionBuilder arguments(Object... arguments) {
            this.arguments = arguments;
            return this;
        }
    }

    public Result<Map> generateErrorResult() {
        if (errorType == null) {
            errorType = CommonErrorTypes.INTERNAL_SERVER_ERROR;
        }

        Result<Map> result = new Result<>();
        result.setStatus(errorType.getCode());
        result.setMsg(getMessage());
        result.setData(Collections.emptyMap());

        return result;
    }

}
