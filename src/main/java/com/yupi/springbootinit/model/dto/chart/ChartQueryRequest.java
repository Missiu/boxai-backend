package com.yupi.springbootinit.model.dto.chart;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

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
     * 图表类型
     */
    private String chatType;

    /**
     * 创建的用户id
     */
    private Long userId;


    private static final long serialVersionUID = 1L;
}