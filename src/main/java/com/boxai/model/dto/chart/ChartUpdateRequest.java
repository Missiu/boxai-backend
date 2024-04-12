package com.boxai.model.dto.chart;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新请求
 */
@Data
public class ChartUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

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

    /**
     * 生成的图标数据
     */
    private String genChart;

    /**
     * 生成分析结论
     */
    private String genResult;

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
    @TableLogic
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}