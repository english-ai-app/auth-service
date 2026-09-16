package com.EnglishApp.auth_service.exception;

import javax.security.sasl.AuthenticationException;

public class ForbiddenAccessException extends AuthenticationException {
    public ForbiddenAccessException(String msg, Throwable t) {
        super(msg, t);
    }

    public ForbiddenAccessException(String msg) {
        super(msg);
    }
}
