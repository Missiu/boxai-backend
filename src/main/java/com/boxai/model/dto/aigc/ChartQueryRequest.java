package com.boxai.model.dto.aigc;

import com.baomidou.mybatisplus.annotation.TableField;
import com.boxai.model.dto.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 * @author Hzh
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}