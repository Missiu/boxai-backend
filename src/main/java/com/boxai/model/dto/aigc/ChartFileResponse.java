package com.boxai.model.dto.aigc;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * ChartFileResponse 类用于封装聊天内容的响应数据
 * @author Hzh
 */
@Data
public class ChartFileResponse implements Serializable {

    /**
     * 目标
     */
    private String goal;

    /**
     * 分析的名称
     */
    private String genName;


    /**
     * ai使用量
     */
    private String usedToken;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 生成的代码注释
     */
    private String codeComment;
    private String codeTechnology;
    private String codeRun;

    /**
     * 生成的代码简介
     */
    private String codeProfile;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
