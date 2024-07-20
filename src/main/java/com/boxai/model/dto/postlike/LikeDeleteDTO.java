package com.boxai.model.dto.postlike;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LikeDeleteDTO implements Serializable {
    /**
     * 用户 id
     */
    private Long userId;
    /**
     * 帖子 id
     */
    @JsonProperty("postId")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long postId;
    @Serial
    private static final long serialVersionUID = 1L;
}
