package com.boxai.model.dto.aigc;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 * @author Hzh
 */
@Data
public class ChartUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 目标
     */
    private String goal;

    /**
     * 分析的名称
     */
    private String genName;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 生成的代码注释
     */
    private String codeComment;

    private String rawData;

    /**
     * 生成的代码简介
     */
    private String codeProfile;

    /**
     * 生成的代码实体
     */
    private String codeEntity;

    /**
     * 生成的代码API
     */
    private String codeAPI;

    /**
     * 生成的代码运行
     */
    private String codeRun;

    /**
     * 生成的代码建议
     */
    private String codeSuggestion;

    /**
     * 代码规范-雷达图
     */
    private String codeNorm;

    /**
     * 代码规范-雷达图说明
     */
    private String codeNormStr;

    /**
     * 代码技术-饼状图
     */
    private String codeTechnology;

    /**
     * 代码目录-树状图
     */
    private String codeCataloguePath;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}