package com.yupi.springbootinit.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class ChartEditRequest implements Serializable {

    /**
     * id
     */
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

    private static final long serialVersionUID = 1L;
}