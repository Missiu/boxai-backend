package com.boxai.api;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.boxai.config.MoonlightApiConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatAPI类用于与外部聊天API进行交互。
 */
@Component
public class MoonlightAPI {
    private static final Logger logger = LoggerFactory.getLogger(MoonlightAPI.class);
    private final ObjectMapper objectMapper = new ObjectMapper(); // 用于JSON序列化和反序列化

    @Autowired
    private MoonlightApiConfig moonlightApiConfig; // API配置信息

    /**
     * 向聊天API发送消息，并返回API的响应。
     *
     * @param systemTxt 系统文本消息
     * @param userTxt 用户文本消息
     * @return API的响应文本
     */
    public String chat(String systemTxt, String userTxt) {
        // 创建聊天请求体
        Map<String, Object> requestBody = createChatRequestBody(systemTxt, userTxt);

        try {
            // 将请求体转换为JSON字符串
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            // 向API发送POST请求
            HttpResponse response = HttpRequest.post(moonlightApiConfig.getUrl())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + moonlightApiConfig.getKey())
                    .body(jsonBody)
                    .execute();

            if (response.isOk()) { // 检查HTTP状态码
                JSONObject jsonObject = JSONUtil.parseObj(response.body());
                String choices = jsonObject.getByPath("choices.message.content").toString();
                logger.info("API Response: {}", choices); // 记录API响应
                return choices;
            } else {
                logger.error("API Request failed with status code: {}", response.getStatus()); // 记录请求失败
                return null; // 或者可选择抛出自定义异常
            }
        } catch (JsonProcessingException e) {
            logger.error("JSON processing error: ", e); // 处理JSON处理异常
            throw new RuntimeException("Failed to process JSON", e);
        }
    }

    /**
     * 创建聊天请求的JSON体。
     *
     * @param systemTxt 系统文本
     * @param userTxt 用户文本
     * @return 请求体Map
     */
    private Map<String, Object> createChatRequestBody(String systemTxt, String userTxt) {
        Map<String, Object> model = new HashMap<>();
        model.put("model", "moonshot-v1-8k"); // 指定使用的模型
        model.put("temperature", 0.3f); // 控制生成结果的随机性

        // 创建系统消息和用户消息
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemTxt);

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userTxt);

        // 将消息放入列表
        List<Map<String, Object>> messages = Arrays.asList(systemMessage, userMessage);

        // 创建最终的请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.putAll(model);
        requestBody.put("messages", messages);

        return requestBody;
    }
}

