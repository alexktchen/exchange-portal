package com.exchange.portal.exchangeportal.common.exception;

public class UnAuthorizedException extends Exception {
    public UnAuthorizedException() {
        super();
    }
    public UnAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
    public UnAuthorizedException(String message) {
        super(message);
    }
    public UnAuthorizedException(Throwable cause) {
        super(cause);
    }
}
