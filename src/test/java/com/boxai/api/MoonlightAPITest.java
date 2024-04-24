package com.boxai.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MoonlightAPITest {
    @Autowired
    private MoonlightAPI moonlightAPI;

    @Test
    void chat() {
        System.out.println(moonlightAPI.chat("你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。", "你好，我叫李雷，1+1等于多少？"));
    }

    @Test
    void token() {
        System.out.println(moonlightAPI.token("你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。","你好，我叫李雷，1+1等于多少？"));
    }
    @Test
    void fetchBalance(){
        System.out.println(moonlightAPI.fetchBalance());
    }
}