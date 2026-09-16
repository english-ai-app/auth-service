//package com.EnglishApp.auth_service.common;
//
//public class ExceedLimitLoginAttemptException extends OAuth2Exception {
//    public ExceedLimitLoginAttemptException(String msg, Throwable t) {
//        super(msg, t);
//    }
//
//    public ExceedLimitLoginAttemptException(String msg) {
//        super(msg);
//        addAdditionalInformation("require_captcha", "true");
//    }
//
//    @Override
//    public String getOAuth2ErrorCode() {
//        return "exceed_limit_attempt";
//    }
//}
