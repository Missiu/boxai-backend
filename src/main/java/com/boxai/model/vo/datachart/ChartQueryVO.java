package com.boxai.model.vo.datachart;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据图表查询VO
 *
 * @author Hzh
 */
@Data
public class ChartQueryVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 结果ID
     */
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
}
