package com.boxai.model.dto.aigc;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通过AI生成图表的请求参数类
 * 用于封装AI生成图表时所需要的请求数据
 * @author Hzh
 */
@Data
public class ChartGenRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1783025723868066981L;
    /**
     * genName 分析数据的名称
     * 用于指定AI需要分析的数据的名称
     */
    private String genName;

    /**
     * goal 分析目标
     * 用于指定AI进行分析时的目标，例如：趋势分析、对比分析等
     */
    private String goal;
}
