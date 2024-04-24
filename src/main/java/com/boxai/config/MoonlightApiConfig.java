package com.boxai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 用于配置Moonlight API的属性类。
 * 该类对应的配置项前缀为"moonlight.api"。
 */
@ConfigurationProperties(prefix = "moonlight.api")
@Data
public class MoonlightApiConfig {
    // Moonlight API的URL地址
    private String url;
    // Moonlight API的访问密钥
    private String key;
    // 用于获取访问令牌的URL地址
    private String tokenUrl;
    // 用户的余额信息
    private String balance;
}

