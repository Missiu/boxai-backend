package com.boxai.model.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户的信息展示类，用于向客户端返回登录用户的脱敏信息。
 *
 * @author Hzh*/
@Data
public class UserInfoResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1656057941309142747L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户账号，用于登录识别
     */
    private String userAccount;

    /**
     * 用户昵称，展示给其他用户看的名称
     */
    private String userName;

    /**
     * 用户头像的URL地址
     */
    private String userAvatar;

    /**
     * 用户的简介信息，可包含个人描述等
     */
    private String userProfile;

    /**
     * 已使用的AI服务令牌数量
     */
    private String usedToken;

    /**
     * 用户拥有的总AI服务令牌数量
     */
    private String token;

    /**
     * 用户的角色，区分普通用户和管理员
     */
    private String userRole;

    /**
     * 用户账号的创建时间
     */
    private Date createTime;

    /**
     * 用户账号信息的最后更新时间
     */
    private Date updateTime;

}
