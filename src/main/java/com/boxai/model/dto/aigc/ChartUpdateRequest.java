package com.boxai.model.dto.aigc;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 更新请求
 * @author Hzh
 */
@Data
public class ChartUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 178230794769133500L; // 序列化ID，用于版本控制

    private Long id; // 数据记录的唯一标识符

    /**
     * 分析数据的名称。用于标识不同的数据分析任务。
     */
    private String genName;

    /**
     * 分析目标。描述进行数据分析的主要目的。
     */
    private String goal;

    /**
     * 原始数据。存储待分析数据的文本或其它格式信息。
     */
    private String chatData;

    /**
     * 生成的代码数据(备用)。用于存储分析过程中可能生成的代码或图表数据。
     */
    private String genChart;

    /**
     * 生成分析结论。存储数据分析后的结论性结果。
     */
    private String genResult;

    /**
     * 创建的用户id。标识哪个用户创建了此数据分析记录。
     */
    private Long userId;

    /**
     * 更新时间。记录此数据分析记录最后一次更新的时间。
     */
    private Date updateTime;

    /**
     * 是否删除。标记此记录是否被标记为删除，通常用于逻辑删除而非物理删除。
     */
    private Integer isDelete;
}