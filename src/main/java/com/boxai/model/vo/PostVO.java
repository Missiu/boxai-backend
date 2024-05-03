package com.boxai.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class PostVO {
    /**
     * id
     */
    private Long id;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 分享描述内容
     */
    private String content;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 帖子 id
     */
    private Long resultId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;
    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 分析的名称
     */
    private String genName;
    /**
     * 生成的代码简介
     */
    private String codeProfile;
}
