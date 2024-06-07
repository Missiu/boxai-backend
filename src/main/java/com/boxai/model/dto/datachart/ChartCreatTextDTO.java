package com.boxai.model.dto.datachart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChartCreatTextDTO implements Serializable {
    /**
     * 目标描述
     */
    private String goalDescription;
    /**
     * 生成名称
     */
    private String generationName;
    /**
     * 文本内容
     */
    private String text;
    @Serial
    private static final long serialVersionUID = 1L;
}
