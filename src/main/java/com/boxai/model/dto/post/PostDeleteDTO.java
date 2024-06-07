package com.boxai.model.dto.post;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子删除
 */
@Data
public class PostDeleteDTO implements Serializable {
    /**
     * 帖子ID
     */
    private Long id;
    @Serial
    private static final long serialVersionUID = 1L;
}
