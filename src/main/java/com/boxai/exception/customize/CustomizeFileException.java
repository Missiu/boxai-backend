package com.boxai.exception.customize;

import com.boxai.common.base.ReturnCode;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 自定义文件异常
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeFileException extends RuntimeException {

    private ReturnCode returnCode;

    private String msg;

    public <T> CustomizeFileException() {
        this.returnCode = ReturnCode.FAIL;
        this.msg = ReturnCode.FAIL.getMsg();
    }

    public <T> CustomizeFileException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    public <T> CustomizeFileException(ReturnCode returnCode, String msg) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg() + " ==> [" + msg + "]";
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}