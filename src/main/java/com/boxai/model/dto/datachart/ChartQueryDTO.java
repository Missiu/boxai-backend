package com.boxai.model.dto.datachart;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 查询请求
 *
 * @author Hzh
 */
@Data
public class ChartQueryDTO implements Serializable {
    /**
     * id
     */
    @JsonProperty("id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;
    /**
     * 目标描述
     */
    private String goalDescription;
    /**
     * 生成名称
     */
    private String generationName;
    /**
     * 用户ID
     */
    private Long userId;
    @Serial
    private static final long serialVersionUID = 1L;
}
