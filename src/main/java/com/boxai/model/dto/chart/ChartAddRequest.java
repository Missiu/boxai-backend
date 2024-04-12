package com.boxai.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 */
@Data
public class ChartAddRequest implements Serializable {
    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图标名称
     */
    private String genName;

    /**
     * 图标数据
     */
    private String chatData;

    /**
     * 图表类型
     */
    private String chatType;

    private static final long serialVersionUID = 1L;
}