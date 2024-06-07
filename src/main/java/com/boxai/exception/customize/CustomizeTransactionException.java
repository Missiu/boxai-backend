package com.boxai.exception.customize;

import com.boxai.common.base.ReturnCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义事务异常
 * 数据库事物处理出现错误，回滚到该异常
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeTransactionException extends RuntimeException {

    private ReturnCode returnCode;

    private String msg;

    public <T> CustomizeTransactionException() {
        this.returnCode = ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE;
        this.msg = ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE.getMsg();
    }

    public <T> CustomizeTransactionException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    public <T> CustomizeTransactionException(ReturnCode returnCode, String msg) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg() + " ==> [" + msg + "]";
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}