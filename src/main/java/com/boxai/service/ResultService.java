package com.boxai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxai.model.domain.Result;
import com.boxai.model.dto.aigc.ChartQueryRequest;

import java.util.List;

/**
 * 图表服务接口，继承自IService<Chart>，提供图表生成和查询条件包装的功能。
 */
public interface ResultService extends IService<Result> {

    /**
     * 根据目标和结果生成图表。
     *
     * @param goal 目标字符串，用于生成图表的目标指引。
     * @param result 结果字符串，包含生成图表所需要的数据或信息。
     * @return 返回图表的字符串表示，可能是URL、Base64编码的图片字符串等。
     */
    List<String> fileAIGC(String goal, String result);

    List<String> filesAIGC(String goal, String fileContent);

    /**
     * 根据图表查询请求获取查询条件包装器。
     *
     * @param chartQueryRequest 包含图表查询条件的数据传输对象。
     * @return 返回配置了查询条件的QueryWrapper<Chart>对象，用于图表数据的查询。
     */
    QueryWrapper<Result> getQueryWrapper(ChartQueryRequest chartQueryRequest);
     String codeExtractor(String code);
    List<String> extractFlagsContent(String text);
}
