package com.boxai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * 数据信息表
 * @TableName data_charts
 */
@TableName(value ="data_charts")
@Data
@Entity
public class DataCharts implements Serializable {
    /**
     * 结果ID
     */
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 目标描述
     */
    private String goalDescription;

    /**
     * 生成名称
     */
    private String generationName;

    /**
     * AI使用量
     */
    private Integer aiTokenUsage;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 代码注释
     */
    private String codeComments;

    /**
     * 原始数据
     */
    private String rawData;

    /**
     * 代码简介
     */
    private String codeProfileDescription;

    /**
     * 代码实体
     */
    private String codeEntities;

    /**
     * 代码API
     */
    private String codeApis;

    /**
     * 代码执行
     */
    private String codeExecution;

    /**
     * 代码建议
     */
    private String codeSuggestions;

    /**
     * 代码规范-雷达图
     */
    private String codeNormRadar;

    /**
     * 代码规范-雷达图说明
     */
    private String codeNormRadarDescription;

    /**
     * 代码技术-饼状图
     */
    private String codeTechnologyPie;

    /**
     * 代码目录-树状图
     */
    private String codeCatalogPath;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否逻辑删除
     */
    private Integer isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}