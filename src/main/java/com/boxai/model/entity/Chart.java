package com.boxai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 图表信息表
 * @TableName chart
 */
@TableName(value ="chart")
@Data
public class Chart implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}