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
     * 分析数据名称
     */
    private String genName;

    /**
     * 原始数据
     */
    private String chatData;


    private static final long serialVersionUID = 1L;
}