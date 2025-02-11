package com.dataMall.userCenter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wx")
@Data
public class WxConfig {
    private String appId;
    private String secret;
    private String server;
    private String qrCodeUrl;
    private String tokenUrl;
    private String openIdUrl;
    private String userInfoUrl;
}
