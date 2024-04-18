package com.boxai.model.dto.chart;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class GenChartByAiRequest1 {
    /**
     *  分析数据的名称
     */
    private String genName;

    /**
     * 分析目标
     */
    private String goal;

    private List<MultipartFile> multipartFiles;

    private static final long serialVersionUID = 1L;
}
