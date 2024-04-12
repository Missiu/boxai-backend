package com.boxai.model.dto.chart;

import lombok.Data;

@Data
public class GenChartByAiRequest {

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


    private static final long serialVersionUID = 1L;
}
