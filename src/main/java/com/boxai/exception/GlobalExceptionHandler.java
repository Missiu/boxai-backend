package com.boxai.exception;

import com.boxai.common.base.HttpStatus;
import com.boxai.common.base.R;
import com.boxai.exception.customize.CustomizeFileException;
import com.boxai.exception.customize.CustomizeLockException;
import com.boxai.exception.customize.CustomizeReturnException;
import com.boxai.exception.customize.CustomizeTransactionException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

/**
 * Security全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 系统异常
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.ERROR, e.getMessage());
    }


    /**
     * 拦截未知的运行时异常
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',发生未知的运行时异常.", requestUri, e);
        return R.fail(HttpStatus.ERROR, e.getMessage());
    }

    /**
     * 请求参数类型不匹配
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.BAD_REQUEST, String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), Objects.isNull(e.getRequiredType()) ? "None" : e.getRequiredType().getName(), e.getValue()));
    }

    /**
     * 方法参数校验异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = Objects.isNull(e.getBindingResult().getFieldError()) ? "No Message" : e.getBindingResult().getFieldError().getDefaultMessage();
        return R.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 自定义文件异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(CustomizeFileException.class)
    public R<String> handleCustomizeFileException(CustomizeFileException e) {
        log.error(e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg(), e);
        int code = e.getReturnCode().getCode();
        String msg = e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg();
        return R.fail(code, msg);
    }

    /**
     * 自定义锁异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(CustomizeLockException.class)
    public R<String> handleCustomizeLockException(CustomizeLockException e) {
        log.error(e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg(), e);
        int code = e.getReturnCode().getCode();
        String msg = e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg();
        return R.fail(code, msg);
    }


    /**
     * 自定义数据库事务异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(CustomizeTransactionException.class)
    public R<String> handleCustomizeTransactionException(CustomizeTransactionException e) {
        log.error(e.getMessage(), e);
        return R.fail(e.getReturnCode());
    }

    /**
     * 自定义返回异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(CustomizeReturnException.class)
    public R<String> handleCustomizeReturnException(CustomizeReturnException e) {
        log.error(e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg(), e);
        int code = e.getReturnCode().getCode();
        String msg = e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg();
        return R.fail(code, msg);
    }
}