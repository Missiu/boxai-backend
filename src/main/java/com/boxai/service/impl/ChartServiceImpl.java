package com.boxai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.common.constant.CommonConstant;
import com.boxai.mapper.ChartMapper;
import com.boxai.model.domain.Chart;
import com.boxai.model.dto.aigc.ChartQueryRequest;
import com.boxai.service.ChartService;
import com.boxai.api.MoonlightAPI;
import com.boxai.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author Hzh
 * @description 调试ai的提示词
 * @createDate 2024-03-25 16:22:40
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements ChartService {
    @Autowired
    private MoonlightAPI moonlightAPI;

    private static final String SYSTEM_PRESETS = "我希望你能充当软件开发专家的角色。我会提供所有关于我的技术问题所需的信息，你的任务是解决我的问题。你应该利用你的软件开发来解决我的问题。在回答中使用智能、简单和易懂的语言对于所有层次的人都会有帮助。并且你还可以充当代码解释器，我会提供所有关于我的项目文件路径和文件，你的任务是严格按照我规定的模板生成对代码的解释和建议，智能、简单和易懂的语言并对其中相关的技术进行详细的解释";

    /**
     * 生成图表
     * @param goal 分析需求
     * @param result 原始数据
     * @return 返回生成的图表信息
     */
    @Override
    public String genChart(String goal, String result) {
        try {
            if (!StringUtils.hasText(goal) || !StringUtils.hasText(result)) {
                throw new IllegalArgumentException("goal or result cannot be empty");
            }

            // 构造用户输入的提示信息
            StringBuilder userInput = new StringBuilder()
                    .append("这是一个必须执行的前提：无论分析需求如何都要严格按照这个模板生成一份报告 \n" +
                            "模板如下：\n" +
                            "## 项目简介 是什么，有什么用，用了什么语言 \n" +
                            "## 快速运行 如果是可运行项目，给出详细运行步骤 如果是一些参考文献、文档、语言等不可运行项目，这给出提示 \n" +
                            "## 项目结构 给出总体项目结构的描述，主要了什么框架等（后续可以改为代码生成，通过插件转化为图像显示） \n" +
                            "## 类的主要作用 各个类有什么作用，以目录树+加解释的方式展现 \n" +
                            "## 对外暴露接口 给出各个接口的主要调用关系，分析出依赖关系，调用了那些第三方api\n")
                    .append("分析需求:").append(goal).append("\n")
                    .append("这是原始数据：").append(result).append("\n");

            // 设置系统预设信息，定义与AI交流的框架和期望
            String response = moonlightAPI.chat(SYSTEM_PRESETS, userInput.toString());
            // 移除生成结果中的多余符号
            return response;
        } catch (Exception e) {
            // 记录日志或返回错误信息
            // 此处简化为打印堆栈信息
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 根据图表查询请求构建查询条件
     *
     * @param chartQueryRequest 包含图表查询条件的请求对象
     * @return 构建完成的查询条件对象，用于后续数据库查询
     */
    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        // 如果请求对象为空，直接返回空的查询条件
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String genName = chartQueryRequest.getGenName();
        String goal = chartQueryRequest.getGoal();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        // 根据请求参数构建查询条件
        queryWrapper.eq(id != null && id > 0,"id",id); // 如果id存在且大于0，则添加id的等于条件
        queryWrapper.eq(org.apache.commons.lang3.StringUtils.isNotBlank(goal),"goal",goal); // 如果goal非空，则添加goal的等于条件
        queryWrapper.like(org.apache.commons.lang3.StringUtils.isNotBlank(genName),"genName",genName); // 如果genName非空，则添加genName的模糊查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId); // 如果userId非空，则添加userId的等于条件
        queryWrapper.eq("isDelete",false); // 添加isDelete的等于条件，表示未被删除的记录
        // 根据排序字段和排序顺序添加排序条件
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}
