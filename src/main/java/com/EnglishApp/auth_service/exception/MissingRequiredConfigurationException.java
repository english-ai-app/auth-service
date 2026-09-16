package com.EnglishApp.auth_service.exception;

public class MissingRequiredConfigurationException extends IllegalStateException {
    public MissingRequiredConfigurationException(String s) {
        super(s);
    }
}
