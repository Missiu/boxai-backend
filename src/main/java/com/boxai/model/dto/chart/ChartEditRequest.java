package com.boxai.model.dto.chart;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 编辑请求
 *
 */
@Data
public class ChartEditRequest implements Serializable {

    private Long id;

    /**
     * 分析数据的名称
     */
    private String genName;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 原始数据
     */
    private String chatData;

    /**
     * 生成的代码数据(备用)
     */
    private String genChart;

    /**
     * 生成分析结论
     */
    private String genResult;

    /**
     * 创建的用户id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;


    private static final long serialVersionUID = 1L;
}