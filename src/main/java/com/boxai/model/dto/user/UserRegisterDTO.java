package com.boxai.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

import static com.boxai.common.constants.CommonConstant.REGEX_NUMBER_AND_LETTER;

/**
 * 用户注册
 *
 * @author Hzh
 */
@Data
public class UserRegisterDTO implements Serializable {
    /**
     * 用户账号。
     */
    @Size(min = 2, max = 16, message = "账号长度介于2-16位之间")
    @NotBlank(message = "账号不能为空")
    @Pattern(regexp = REGEX_NUMBER_AND_LETTER, message = "账户名称包含特殊字符")
    private String userAccount;

    /**
     * 用户密码。
     */
    @Size(min = 5, max = 16, message = "密码长度介于5-16位之间")
    @NotBlank(message = "密码不能为空")
    private String userPassword;

    /**
     * 确认密码。
     */
    @NotBlank(message = "二次密码不能为空")
    private String checkPassword;

    @Serial
    private static final long serialVersionUID = 1L;
}
