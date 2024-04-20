package com.boxai.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求数据传输对象
 * 用于接收前端发送的用户注册请求信息
 * @author Hzh
 */
@Data
public class UserRegisterRequest implements Serializable {
    /**
     * 序列化版本ID，用于版本控制。
     */
    @Serial
    private static final long serialVersionUID = -92904383887374550L;

    /**
     * 用户账号。
     */
    private String userAccount;

    /**
     * 用户密码。
     */
    private String userPassword;

    /**
     * 确认密码，用于验证用户输入的密码是否一致。
     */
    private String checkPassword;
}
