package com.boxai.exception;

import com.boxai.common.enums.ErrorCode;

/**
 * 自定义异常类，用于处理业务逻辑中发生的异常情况。
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码，用于标识异常的类型。
     */
    private final int code;

    /**
     * 构造函数，接收一个ErrorCode枚举作为参数，初始化异常信息和错误码。
     * @param errorCode 错误码枚举，包含错误代码和错误信息。
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 使用ErrorCode枚举中的错误信息初始化RuntimeException
        this.code = errorCode.getCode(); // 初始化错误码
    }

    /**
     * 构造函数，接收一个ErrorCode枚举和自定义错误信息作为参数，初始化异常信息和错误码。
     * @param errorCode 错误码枚举，包含错误代码。
     * @param message 自定义的错误信息。
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message); // 使用自定义错误信息初始化RuntimeException
        this.code = errorCode.getCode(); // 初始化错误码
    }

    /**
     * 获取错误码的方法。
     * @return 错误码。
     */
    public int getCode() {
        return code;
    }
}
