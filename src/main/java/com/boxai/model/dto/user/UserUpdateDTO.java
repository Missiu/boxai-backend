package com.boxai.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新
 *
 */
@Data
public class UserUpdateDTO implements Serializable {
    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户简介
     */
    private String profile;

    /**
     * 账号
     */
    private String userAccount;
    @Serial
    private static final long serialVersionUID = 1L;
}
