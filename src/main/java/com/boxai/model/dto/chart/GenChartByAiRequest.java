package com.boxai.model.dto.chart;

import lombok.Data;

@Data
public class GenChartByAiRequest {

    /**
     *  分析数据的名称
     */
    private String genName;

    /**
     * 分析目标
     */
    private String goal;


    private static final long serialVersionUID = 1L;
}
