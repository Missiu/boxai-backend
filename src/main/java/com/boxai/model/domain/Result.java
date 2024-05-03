package com.boxai.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据信息表
 * @TableName result
 */
@TableName(value ="result")
@Data
public class Result implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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

    /**
     * 原始数据
     */
    private String rawData;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}