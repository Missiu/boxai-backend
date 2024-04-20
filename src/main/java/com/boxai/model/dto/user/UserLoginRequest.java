package com.boxai.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 用户登录请求类，用于封装登录请求的数据。
 * 实现Serializable接口，允许对象进行序列化。
 * @author Hzh
 */
@Data
public class UserLoginRequest implements Serializable {
    /**
     * 序列化ID，用于版本控制。
     */
    @Serial
    private static final long serialVersionUID = 8327400718996622465L;
    /**
     * 用户账号，用于登录验证。
     */
    private String userAccount;
    /**
     * 用户密码，用于登录验证。
     */
    private String userPassword;
}


