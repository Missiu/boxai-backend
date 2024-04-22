package com.boxai.exception;

import com.boxai.model.dto.common.BaseResponse;
import com.boxai.common.enums.ErrorCode;
import com.boxai.model.dto.common.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，用于捕获并处理异常。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     * @param e 抛出的业务异常
     * @return 返回一个包含错误信息的响应对象
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        // 根据业务异常编码和消息返回错误响应
        return ResultResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理运行时异常。
     * @param e 抛出的运行时异常
     * @return 返回一个包含系统错误信息的响应对象
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        // 返回一个通用的系统错误响应
        return ResultResponse.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
