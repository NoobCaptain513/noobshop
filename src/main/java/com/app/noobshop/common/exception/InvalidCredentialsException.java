package com.app.noobshop.common.exception;

/**
 * 凭据无效异常
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
