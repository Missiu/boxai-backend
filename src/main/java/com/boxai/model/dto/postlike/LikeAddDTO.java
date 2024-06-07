package com.boxai.model.dto.postlike;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LikeAddDTO implements Serializable {
    /**
     * 用户 id
     */
    private Long userId;
    /**
     * 帖子 id
     */
    private Long postId;
    @Serial
    private static final long serialVersionUID = 1L;
}
