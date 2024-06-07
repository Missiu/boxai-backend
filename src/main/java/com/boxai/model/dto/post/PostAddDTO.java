package com.boxai.model.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostAddDTO implements Serializable {
    /**
     * 作品id
     */
    @NonNull
    private Long chartId;
    /**
     * 分享描述内容
     */
    private String content;
    @Serial
    private static final long serialVersionUID = 1L;
}
