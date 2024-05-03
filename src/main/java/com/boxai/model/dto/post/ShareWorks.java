package com.boxai.model.dto.post;

import lombok.Data;

@Data
public class ShareWorks {
    /**
     * 作品id
     */
    private Long id;
    /**
     * 分享描述内容
     */
    private String content;
}
