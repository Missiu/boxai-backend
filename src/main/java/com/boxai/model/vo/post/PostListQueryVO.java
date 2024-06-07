package com.boxai.model.vo.post;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class PostListQueryVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;

    /**
     * 点赞数
     */
    private Integer likesCount;

    /**
     * 收藏数
     */
    private Integer favoritesCount;

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
    private Long postId;

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
    private String nickname;

    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 分析的名称
     */
    private String generationName;
    /**
     * 生成的代码简介
     */
    private String codeProfileDescription;
}
