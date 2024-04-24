package com.boxai.api;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.boxai.config.MoonlightApiConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
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

public double token(String systemTxt, String userTxt) {
    // 创建聊天请求体
    Map<String, Object> requestBody = createChatRequestBody(systemTxt, userTxt);

    try {
        // 将请求体转换为JSON字符串
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        // 向API发送POST请求
        HttpResponse response = HttpRequest.post("https://api.moonshot.cn/v1/tokenizers/estimate-token-count")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + moonlightApiConfig.getKey())
                .body(jsonBody)
                .execute();

        if (response.isOk()) { // 检查HTTP状态码
            JSONObject jsonObject = JSONUtil.parseObj(response.body());
            String totalTokensStr = jsonObject.getByPath("data.total_tokens").toString();
            double totalTokens = Double.parseDouble(totalTokensStr);
            logger.info("API Response: {}", totalTokens); // 记录API响应
            return totalTokens;
        } else {
            logger.error("API Request failed with status code: {}", response.getStatus()); // 记录请求失败
            throw new RuntimeException("Failed to process JSON");
        }
    } catch (JsonProcessingException e) {
        logger.error("JSON processing error: ", e); // 处理JSON处理异常
        throw new RuntimeException("Failed to process JSON", e);
    } catch (NumberFormatException e) {
        logger.error("Invalid double value in API response: ", e);
        throw new RuntimeException("Failed to parse double value from API response", e);
    }
}


    /**
     * 获取账户余额信息。
     * 该方法会从指定API获取账户的可用余额、礼券余额和现金余额，并将这些信息存储在一个Map中返回。
     *
     * @return Map<String, Double> 包含账户余额信息的Map，其中键包括"available_balance"、"voucher_balance"和"cash_balance"。
     */
    public Map<String, Double> fetchBalance() {
        Map<String, Double> balances = new HashMap<>();

        try {
            // 发送GET请求
            HttpResponse response = HttpUtil.createGet(moonlightApiConfig.getBalance())
                    .header("Authorization", "Bearer " + moonlightApiConfig.getKey())
                    .execute();

            // 检查响应状态
            if (response.getStatus() == 200) {
                // 读取并解析响应体为JSON
                String responseBody = IoUtil.read(response.bodyStream(), StandardCharsets.UTF_8);
                JSONObject json = JSONUtil.parseObj(responseBody);

                // 提取数据部分
                JSONObject data = json.getJSONObject("data");

                // 获取三个余额值
                double availableBalance = data.getDouble("available_balance");
                double voucherBalance = data.getDouble("voucher_balance");
                double cashBalance = data.getDouble("cash_balance");

                // 将余额信息添加到balances Map中
                balances.put("available_balance", availableBalance);
                balances.put("voucher_balance", voucherBalance);
                balances.put("cash_balance", cashBalance);

                logger.info("获取到的余额信息: {}", balances);
            } else {
                logger.error("获取余额失败。响应状态: {}", response.getStatus());
            }
        } catch (Exception e) {
            logger.error("获取余额时发生错误:", e);
        }

        return balances;
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
//        model.put("model", "moonshot-v1-32k"); // 指定使用的模型
        model.put("model", "moonshot-v1-128k"); // 指定使用的模型
        model.put("temperature", 0.2f); // 控制生成结果的随机性

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

