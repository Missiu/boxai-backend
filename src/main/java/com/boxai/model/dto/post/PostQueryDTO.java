package com.boxai.model.dto.post;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PostQueryDTO implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 分析的名称
     */
    private String generationName;
    /**
     * 生成的代码简介
     */
    private String codeProfileDescription;

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
     * 用户昵称
     */
    private String nickname;
    @Serial
    private static final long serialVersionUID = 1L;
}
