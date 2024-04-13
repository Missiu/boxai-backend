package com.boxai.model.dto.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Hzh
 */
@Data
public class ChatCompletionRequest implements Serializable {
    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<Message> messages;

    @JsonProperty("temperature")
    private double temperature;

    @Data
    public static class Message {
        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private String content;
    }
}
