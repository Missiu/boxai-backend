package com.boxai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "moonlight.api")
@Data
public class MoonlightApiConfig {
    private String url;
    private String key;
}
