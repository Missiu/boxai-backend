package com.boxai.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录
 *
 * @author Hzh
 */
@Data
public class UserLoginDTO implements Serializable {
    /**
     * 用户账号，用于登录验证。
     */
    @NotBlank(message = "账号不能为空")
    private String userAccount;
    /**
     * 用户密码，用于登录验证。
     */
    @NotBlank(message = "密码不能为空")
    private String userPassword;
    @Serial
    private static final long serialVersionUID = 1L;
}
