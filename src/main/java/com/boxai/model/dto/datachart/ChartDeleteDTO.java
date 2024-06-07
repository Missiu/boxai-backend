package com.boxai.model.dto.datachart;

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
    private Long id;
    @Serial
    private static final long serialVersionUID = 1L;
}