package com.EnglishApp.auth_service.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("oauth.properties")
public class OauthProperties {
    private String secretKey;
    private int defaultAccessTokenTimeout = 3600;
    private int defaultRefreshTokenTimeout = 84600;

}
