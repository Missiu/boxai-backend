package com.boxai.model.dto.postfavorite;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 帖子收藏列表查询
 */
@Data
public class FavoriteQueryDTO implements Serializable {
    /**
     * 用户 id
     */
    private Long userId;
    @Serial
    private static final long serialVersionUID = 1L;
}
