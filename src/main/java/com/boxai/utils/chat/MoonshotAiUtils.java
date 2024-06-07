package com.boxai.utils.chat;

import cn.hutool.http.*;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.boxai.common.base.ReturnCode;
import com.boxai.exception.customize.CustomizeReturnException;
import com.boxai.exception.customize.CustomizeTransactionException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.thread.ThreadUtil.sleep;
import static com.boxai.common.constants.MoonshotAiConstant.*;

/**
 * ChatAPI类用于与外部聊天API进行交互。
 */

@Slf4j
public class MoonshotAiUtils {
    // Moonlight API的URL地址
    private static final String API_KEY = "sk-o5jNed8HHSua4ch2pE4vFabKnB8AswQVwRXS9Ne30Ti1zR9T";
    private static final String MODELS_URL = "https://api.moonshot.cn/v1";
    private static final String FILES_URL = "https://api.moonshot.cn/v1/files";
    private static final String ESTIMATE_TOKEN_COUNT_URL = "https://api.moonshot.cn/v1/tokenizers/estimate-token-count";
    private static final String CHAT_COMPLETION_URL = "https://api.moonshot.cn/v1/chat/completions";
    private static final String GET_BALANCE_URL = "https://api.moonshot.cn/v1/users/me/balance";


    /**
     * 获取模型列表。
     *
     * @return 返回模型列表的信息，格式为字符串。
     */
    public static String getModelList() {
        return getCommonRequest(MODELS_URL)
                .execute()
                .body();
    }

    /**
     * 上传文件。
     *
     * @param file 需要上传的文件对象，不能为空。
     * @return 返回上传结果的信息，格式为字符串。
     */
    public static String uploadFile(@NonNull File file) {
        return getCommonRequest(FILES_URL)
                .method(Method.POST)
                .header("purpose", "file-extract")
                .form("file", file)
                .execute()
                .body();
    }

    /**
     * 获取文件列表。
     *
     * @return 返回文件列表的信息，格式为字符串。
     */
    public static String getFileList() {
        return getCommonRequest(FILES_URL)
                .execute()
                .body();
    }

    /**
     * 删除指定的文件。
     *
     * @param fileId 需要删除的文件ID，不能为空。
     * @return 返回删除操作的结果信息，格式为字符串。
     */
    public static String deleteFile(@NonNull String fileId) {
        return getCommonRequest(FILES_URL + "/" + fileId)
                .method(Method.DELETE)
                .execute()
                .body();
    }

    /**
     * 获取指定文件的详细信息。
     *
     * @param fileId 需要获取信息的文件ID，不能为空。
     * @return 返回文件详细信息，格式为字符串。
     */
    public static String getFileDetail(@NonNull String fileId) {
        return getCommonRequest(FILES_URL + "/" + fileId)
                .execute()
                .body();
    }

    /**
     * 获取指定文件的内容。
     *
     * @param fileId 需要获取内容的文件ID，不能为空。
     * @return 返回文件内容，格式为字符串。
     */
    public static String getFileContent(@NonNull String fileId) {
        return getCommonRequest(FILES_URL + "/" + fileId + "/content")
                .execute()
                .body();
    }

    /**
     * 估算消息的token数量。
     *
     * @param model    使用的模型，不能为空。
     * @param messages 需要估算token数量的消息列表，不能为空。
     * @return 返回估算的token数量，格式为字符串。
     */
    public static String estimateTokenCount(@NonNull String model, @NonNull List<Message> messages) {
        String requestBody = new JSONObject()
                .putOpt("model", model)
                .putOpt("messages", messages)
                .toString();
        return getCommonRequest(ESTIMATE_TOKEN_COUNT_URL)
                .method(Method.POST)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body(requestBody)
                .execute()
                .body();
    }

    private static HttpRequest getCommonRequest(@NonNull String url) {
        return HttpRequest.of(url).header(Header.AUTHORIZATION, "Bearer " + API_KEY);
    }

    public static String chat(@NonNull String model, @NonNull List<Message> messages, String apiKey) {
        apiKey = apiKey == null ? API_KEY : apiKey;
        // 构建请求体
        String requestBody = new JSONObject()
                .putOpt("model", model)
                .putOpt("messages", messages)
                .putOpt("max_tokens", 4096)
                .putOpt("stream", false)
                .toString();

        HttpResponse response = HttpRequest.post(CHAT_COMPLETION_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .body(requestBody)
                .execute();

        // 检查HTTP状态码并处理响应
        if (response.isOk()) {
            JSONObject jsonObject = JSONUtil.parseObj(response.body());
            String usedToken = JSONUtil.parseObj(estimateTokenCount(model, messages)).getByPath("data.total_tokens").toString().replaceAll("^\\[|\\]$", "");
            return jsonObject.getByPath("choices.message.content").
                    toString().replaceAll("^\\[|\\]$", "")
                    + "\n 【【usedToken】】 \n" + usedToken;
        } else {
            log.error("Request to {} failed with status code {}", CHAT_COMPLETION_URL, response.getStatus());
            return null;
        }
    }

    // 使用默认的apiKey
    public static String FileUserChatUtil(List<Message> messages, String apiKey) {
        return chat(MODEL_NAME_8k, messages, apiKey);
    }

    public static String MultipleFilesUserChatUtil(List<Message> messages, String apiKey) {
        return chat(MODEL_NAME_32k, messages, apiKey);
    }

    // 使用自定义的apiKey
    public static String CustomizeUserChatUtil(List<Message> messages, String apiKey) {
        try {
            return chat(MODEL_NAME_32k, messages, apiKey);
        }catch (Exception e){
            throw new CustomizeReturnException(ReturnCode.FAIL, "apiKey错误或未生效,AI调用失败");
        }
    }

    // 为VIP用户使用不同的模型
    public static String VIPUserChatUtil(List<Message> messages, String apiKey) {
        return chat(MODEL_NAME_128k, messages, apiKey);
    }

    public static Map<String, Double> fetchBalance(String apiKey) {
        Map<String, Double> balances = new HashMap<>();
        HttpResponse response = HttpUtil.createGet(GET_BALANCE_URL)
                .header("Authorization", "Bearer " + apiKey)
                .execute();
        if (response.isOk()) {
            JSONObject json = JSONUtil.parseObj(response);
            JSONObject data = json.getJSONObject("data");
            // 获取三个余额值
            double availableBalance = data.getDouble("available_balance");
            double voucherBalance = data.getDouble("voucher_balance");
            double cashBalance = data.getDouble("cash_balance");

            // 将余额信息添加到balances Map中
            balances.put("available_balance", availableBalance);
            balances.put("voucher_balance", voucherBalance);
            balances.put("cash_balance", cashBalance);
        } else {
            log.error("Request to {} failed with status code {}", GET_BALANCE_URL, response.getStatus());
        }
        return balances;
    }


//    static Map<String, List<Message>> userConversations = new ConcurrentHashMap<>();
//
//    public static synchronized String chatWithHistory(@NonNull String model, @NonNull String userId, String MoonshotAiPrompt, String apiKey) {
//        apiKey = apiKey == null ? API_KEY : apiKey;
//        // 构建历史消息列表
//        // 根据用户ID获取或初始化消息列表
//        List<Message> messages = userConversations.computeIfAbsent(userId, k -> new ArrayList<>());
//        messages.add(new Message("system", "你是 Kimi，由 Moonshot AI 提供的人工智能助手..."));
//        messages.add(new Message("user", MoonshotAiPrompt));
//        // 构建请求体
//        JSONObject requestBody = new JSONObject();
//        requestBody.putOpt("model", model);
//        requestBody.putOpt("messages", messages);
//        requestBody.putOpt("stream", false);
//
//        // 发送请求并获取响应
//        HttpResponse response = HttpRequest.post(CHAT_COMPLETION_URL)
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + apiKey)
//                .body(requestBody.toString())
//                .execute();
//        // 更新用户的对话历史
//        userConversations.put(userId, messages);
//        // 检查HTTP状态码并处理响应
//        if (response.isOk()) {
//            JSONObject jsonObject = JSONUtil.parseObj(response.body());
//            String content = jsonObject.getByPath("choices[0].message.content").toString();
//            return content.replaceAll("^\\[|\\]$", ""); // 移除首尾的方括号
//        } else {
//            // 这里可以根据需要添加日志记录
//            System.err.println("Request to " + CHAT_COMPLETION_URL + " failed with status code " + response.getStatus());
//            return null;
//        }
//    }
}

