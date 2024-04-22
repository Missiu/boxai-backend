package com.boxai.utils;

import com.boxai.api.MoonlightAPI;
import com.boxai.config.MoonlightApiConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatAPITest {
    @Autowired
    private MoonlightApiConfig moonlightApiConfig;
    @Autowired
    private MoonlightAPI moonlightAPI;


    @Test
    void chat() {
        System.out.println(moonlightApiConfig.getKey());
        System.out.println(moonlightApiConfig.getUrl());

        System.out.println(moonlightAPI.chat("你是谁", "你是谁"));
    }
}