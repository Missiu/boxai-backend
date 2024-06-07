package com.boxai.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户修改密码
 */
@Data
public class UserUpdatePasswordDTO implements Serializable {
    /**
     * 新密码
     */
    @Size(min = 5, max = 16, message = "密码长度介于5-16位之间")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
    /**
     * 二次密码
     */
    @Size(min = 5, max = 16, message = "密码长度介于5-16位之间")
    @NotBlank(message = "二次密码不能为空")
    private String checkPassword;
    /**
     * 原密码
     */
    @Size(min = 5, max = 16, message = "密码长度介于5-16位之间")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    @Serial
    private static final long serialVersionUID = 1L;
}
