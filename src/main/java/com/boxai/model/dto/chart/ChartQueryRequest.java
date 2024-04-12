package com.boxai.model.dto.chart;

import com.boxai.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图标名称
     */
    private String genName;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chatType;

    /**
     * 创建的用户id
     */
    private Long userId;


    private static final long serialVersionUID = 1L;
}