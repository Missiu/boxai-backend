package com.boxai.model.dto.datachart;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新请求
 *
 * @author Hzh
 */
@Data
public class ChartUpdateDTO implements Serializable {
    /**
     * id
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
     * 代码注释
     */
    private String codeComments;
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
    @Serial
    private static final long serialVersionUID = 1L;
}
