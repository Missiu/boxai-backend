package com.boxai.model.dto.aigc;

import com.boxai.model.dto.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 查询请求
 * @author Hzh
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -7289543092961478777L;
    /**
     * id
     */
    private Long id;

    /**
     * 数据名称
     */
    private String genName;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 创建的用户id
     */
    private Long userId;

}