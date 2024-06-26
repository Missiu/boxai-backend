package com.boxai.exception.customize;

import com.boxai.common.base.ReturnCode;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 自定义锁异常
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeLockException extends RuntimeException {

    private ReturnCode returnCode;

    private String msg;

    public <T> CustomizeLockException() {
        this.returnCode = ReturnCode.FAIL;
        this.msg = ReturnCode.FAIL.getMsg();
    }

    public <T> CustomizeLockException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    public <T> CustomizeLockException(ReturnCode returnCode, String msg) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg() + " ==> [" + msg + "]";
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}