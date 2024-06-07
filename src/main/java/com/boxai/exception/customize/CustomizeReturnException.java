package com.boxai.exception.customize;

import com.boxai.common.base.ReturnCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义返回错误异常类
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeReturnException extends RuntimeException {

    private ReturnCode returnCode;

    private String msg;

    public <T> CustomizeReturnException() {
        this.returnCode = ReturnCode.FAIL;
        this.msg = ReturnCode.FAIL.getMsg();
    }

    public <T> CustomizeReturnException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    public <T> CustomizeReturnException(ReturnCode returnCode, String msg) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg() + " ==> [" + msg + "]";
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}