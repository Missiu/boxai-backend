package com.boxai.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.boxai.model.dto.chart.ChatCompletionRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ChatAPI {
    private static final String MOONSHOT_API_URL = "https://api.moonshot.cn/v1/chat/completions";

    private static final String MOONSHOT_API_KEY = "sk-4lLjQlxipwp1G0CEZIZIrdfEZ39zQkQj25j8TdaZKIC3FinG"; // 替换为您的实际API密钥

    public static String Chat( String systemTxt,String userTxt) {
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel("moonshot-v1-8k");

        ChatCompletionRequest.Message systemMessage = new ChatCompletionRequest.Message();
        systemMessage.setRole("system");
        systemMessage.setContent(systemTxt);

        ChatCompletionRequest.Message userMessage = new ChatCompletionRequest.Message();
        userMessage.setRole("user");
        userMessage.setContent(userTxt);

        request.setMessages(Arrays.asList(systemMessage, userMessage));
        request.setTemperature(0.3);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonBody = mapper.writeValueAsString(request);
            HttpResponse response = HttpRequest.post(MOONSHOT_API_URL)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + MOONSHOT_API_KEY)
                    .body(jsonBody)
                    .execute();
//            System.out.println(response.body());
            JSONObject jsonObject = JSONUtil.parseObj(response.body());
            String choices = jsonObject.getByPath("choices.message.content").toString();
//            System.out.println(choices);
            return choices;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
