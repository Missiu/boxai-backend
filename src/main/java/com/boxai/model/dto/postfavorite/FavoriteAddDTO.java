package com.boxai.model.dto.postfavorite;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子收藏
 */
@Data
public class FavoriteAddDTO implements Serializable {
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
