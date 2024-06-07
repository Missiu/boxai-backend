package com.boxai.model.dto.datachart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建请求
 *
 * @author Hzh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartCreateDTO implements Serializable {
    /**
     * 目标描述
     */
    private String goalDescription;
    /**
     * 生成名称
     */
    private String generationName;
    @Serial
    private static final long serialVersionUID = 1L;
}
