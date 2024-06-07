package com.boxai.utils.chat;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MoonshotAiPrompt {
    @Value("${templates.codeTemplate}")
    private String codeTemplate;

    @Value("${system.presets}")
    private String systemPresets;
}
