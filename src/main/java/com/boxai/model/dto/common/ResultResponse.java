package com.boxai.model.dto.common;

import com.boxai.common.enums.ErrorCode;

/**
 * 返回工具类
 */
public class ResultResponse {

    /**
     * 创建一个表示成功响应的对象。
     *
     * @param <T> 响应数据的类型。
     * @param data 成功时返回的数据。
     * @return 返回一个包含成功状态、数据和消息的BaseResponse对象。
     */
    public static <T> BaseResponse<T> success(T data) {
        // 构造一个成功响应，包含状态码、数据和"ok"消息
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 生成一个错误响应对象。
     * @param errorCode 错误代码枚举，代表具体的错误类型。
     * @return 返回一个包含错误信息的BaseResponse对象。
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 生成一个自定义错误响应对象。
     * @param code 错误代码，用整数表示。
     * @param message 错误消息，对错误进行描述。
     * @return 返回一个包含自定义错误代码和消息的BaseResponse对象。
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 生成一个表示错误的响应对象。
     *
     * @param errorCode 错误码对象，包含错误的代码和相关描述。
     * @param message 错误详情或提示信息。
     * @return 返回一个包含错误码、空数据和错误消息的BaseResponse对象。
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        // 创建并返回一个错误响应对象
        return new BaseResponse(errorCode.getCode(), null, message);
    }
}
