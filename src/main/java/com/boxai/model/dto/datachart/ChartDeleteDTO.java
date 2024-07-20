package com.boxai.model.dto.datachart;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 删除请求
 *
 * @author Hzh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDeleteDTO implements Serializable {
    /**
     * id
     */
    @JsonProperty("id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;
    @Serial
    private static final long serialVersionUID = 1L;
}