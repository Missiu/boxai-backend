package com.boxai.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户查询参数
 *
 */
@Data
public class UserQueryDTO implements Serializable {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户昵称
     */
    private String nickname;
    @Serial
    private static final long serialVersionUID = 1L;
}
