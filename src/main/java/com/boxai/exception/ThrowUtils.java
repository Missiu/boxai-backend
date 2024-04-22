package com.boxai.exception;

import com.boxai.common.enums.ErrorCode;

/**
 * 抛异常工具类，提供条件抛出异常的便捷方法。
 */
public class ThrowUtils {

    /**
     * 当条件为真时抛出运行时异常。
     *
     * @param condition 条件，如果为真则抛出异常。
     * @param runtimeException 需要抛出的运行时异常实例。
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 当条件为真时抛出业务异常，业务异常由ErrorCode定义错误码。
     *
     * @param condition 条件，如果为真则抛出异常。
     * @param errorCode 错误码枚举，定义了错误的类型和代码。
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 当条件为真时抛出业务异常，业务异常由ErrorCode和自定义消息定义。
     *
     * @param condition 条件，如果为真则抛出异常。
     * @param errorCode 错误码枚举，定义了错误的类型和代码。
     * @param message 自定义错误消息。
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
