package com.boxai.model.dto.aigc;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ChartContentResponse 类用于封装聊天内容的响应数据
 * @author Hzh
 */
@Data
public class ChartContentResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 3005890066237764957L;
    /**
     * 用于存储总结性的数据结果
     */
    private String genResult;
}
