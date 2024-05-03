package com.boxai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.api.MoonlightAPI;
import com.boxai.common.constant.CommonConstant;
import com.boxai.common.enums.ErrorCode;
import com.boxai.exception.ThrowUtils;
import com.boxai.mapper.ResultMapper;
import com.boxai.model.domain.Result;
import com.boxai.model.dto.aigc.ChartQueryRequest;
import com.boxai.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.boxai.common.constant.PromptConstant.*;
import static com.boxai.utils.FileUtils.extractFlagsContent;

/**
 * @author Hzh
 * @description 调试ai的提示词
 * @createDate 2024-03-25 16:22:40
 */
@Service
public class ResultServiceImpl extends ServiceImpl<ResultMapper, Result> implements com.boxai.service.ResultService {
    @Autowired
    private MoonlightAPI moonlightAPI;


    /**
     * 基于给定的目标和文件内容，通过AIGC进行单文件分析处理。
     *
     * @param goal        分析需求的目标描述，不能为空。
     * @param fileContent 需要进行分析的文件内容，不能为空。
     * @return 返回包含分析结果和token的字符串列表。
     */
    @Override
    public List<String> fileAIGC(String goal, String fileContent) {
        // 检查参数是否为空，如果为空则抛出异常
        ThrowUtils.throwIf(ObjectUtils.isEmpty(goal), ErrorCode.PARAMS_ERROR, "分析需求不能为空");
        ThrowUtils.throwIf(ObjectUtils.isEmpty(fileContent), ErrorCode.PARAMS_ERROR, "文件内容不能为空");

        // 构建输入字符串，包括固定的代码要求和模板以及具体的文件内容
        String input = FILE_CODE_REQUIRE + FILE_TEMPLATE +
                "\n 分析目标如下： \n" + goal +
                "\n 分析内容如下：\n" + fileContent  ;
        // 通过moonlightAPI的token方法算出token
        double token = moonlightAPI.token(SYSTEM_PRESETS, input);

        // 用构建的输入字符串进行对话处理，获取响应结果
        String response = moonlightAPI.chat(SYSTEM_PRESETS, input);

        // 从响应结果中提取标志内容
        List<String> list = extractFlagsContent(response);

        // 将token转换为字符串并添加到返回结果列表中
        list.add(Double.toString(token));

        return list;
    }


    /**
     * 基于给定的目标和文件内容，通过AIGC进行多文件分析处理。
     *
     * @param goal        分析目标，用于指导分析过程。
     * @param fileContent 文件内容，作为分析的输入。
     * @return 返回一个包含生成的AIGC文件内容的列表。
     */
    @Override
    public List<String> filesAIGC(String goal, String fileContent) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(goal), ErrorCode.PARAMS_ERROR, "分析需求不能为空");
        ThrowUtils.throwIf(ObjectUtils.isEmpty(fileContent), ErrorCode.PARAMS_ERROR, "文件内容不能为空");

        String firstInput = FILES_FIRST_CODE_REQUIRE + FILES_FIRST_TEMPLATE +
                "\n 分析内容如下：\n" + fileContent;
        String secondInput = FILES_SECOND_CODE_REQUIRE + FILES_SECOND_TEMPLATE +
                "\n 分析内容如下：\n" + fileContent;

        double token = moonlightAPI.token(SYSTEM_PRESETS, firstInput) + moonlightAPI.token(SYSTEM_PRESETS, secondInput);
        String firstResponse = moonlightAPI.chat(SYSTEM_PRESETS, firstInput);
        String secondResponse = moonlightAPI.chat(SYSTEM_PRESETS, secondInput);
        List<String> list1 = extractFlagsContent(firstResponse);
        List<String> list2 = extractFlagsContent(secondResponse);
        List<String> arrayList = new ArrayList<>();
        arrayList.addAll(list1);
        arrayList.addAll(list2);
        arrayList.add(Double.toString(token));
        return arrayList;
    }

    /**
     * 根据图表查询请求构建查询条件
     *
     * @param chartQueryRequest 包含图表查询条件的请求对象
     * @return 构建完成的查询条件对象，用于后续数据库查询
     */
    @Override
    public QueryWrapper<Result> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Result> queryWrapper = new QueryWrapper<>();

        Long id = chartQueryRequest.getUserId();
        String genName = chartQueryRequest.getGenName();
        String goal = chartQueryRequest.getGoal();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        // 根据请求参数构建查询条件
//        queryWrapper.eq(id != null && id > 0, "id", id); // 如果id存在且大于0，则添加id的等于条件
        queryWrapper.eq(org.apache.commons.lang3.StringUtils.isNotBlank(goal), "goal", goal); // 如果goal非空，则添加goal的等于条件
        queryWrapper.like(org.apache.commons.lang3.StringUtils.isNotBlank(genName), "genName", genName); // 如果genName非空，则添加genName的模糊查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId); // 如果userId非空，则添加userId的等于条件
        queryWrapper.eq("isDelete", false); // 添加isDelete的等于条件，表示未被删除的记录
        // 根据排序字段和排序顺序添加排序条件
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}
