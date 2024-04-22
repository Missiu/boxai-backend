package com.boxai.model.dto.aigc;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 删除请求
 *
 * @author Hzh
 */
@Data
public class ChartDeleteRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 4243025516665839859L;
    /**
     * id
     */
    private Long id;

}