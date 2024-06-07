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
public class UserUpdateKeyDTO implements Serializable {
    /**
     * 新密码
     */
    private String role;
    @Serial
    private static final long serialVersionUID = 1L;
}
