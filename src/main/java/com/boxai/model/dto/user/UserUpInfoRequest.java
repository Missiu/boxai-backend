package com.boxai.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新请求
 * @author Hzh
 */
@Data
public class UserUpInfoRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -8308112010793318490L;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 账号
     */
    private String userAccount;

}