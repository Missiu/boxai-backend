package com.boxai.model.dto.user;

import lombok.Data;

@Data
public class UserUpPassword {
    /**
     * 密码
     */
    private String newPassword;
    /**
     * 密码
     */
    private String checkPassword;
    /**
     * 密码
     */
    private String oldPassword;
}
