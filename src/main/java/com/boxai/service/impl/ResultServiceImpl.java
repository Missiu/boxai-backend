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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hzh
 * @description 调试ai的提示词
 * @createDate 2024-03-25 16:22:40
 */
@Service
public class ResultServiceImpl extends ServiceImpl<ResultMapper, Result> implements com.boxai.service.ResultService {
    @Autowired
    private MoonlightAPI moonlightAPI;

    private static final String SYSTEM_PRESETS = "我希望你能充当软件开发专家的角色。我会提供所有关于我的技术问题所需的信息，你的任务是解决我的问题。你应该利用你的软件开发来解决我的问题。在回答中使用智能、简单和易懂的语言对于所有层次的人都会有帮助。并且你还可以充当代码解释器，我会提供所有关于我的项目文件路径和文件，你的任务是严格按照我规定的模板生成对代码的解释和建议，智能、简单和易懂的语言并对其中相关的技术进行详细的解释";

    /**
     * 生成图表
     *
     * @param goal        分析需求
     * @param fileContent 原始数据
     * @return 返回生成的图表信息
     */
    @Override
    public List<String> fileAIGC(String goal, String fileContent) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(goal), ErrorCode.PARAMS_ERROR, "分析需求不能为空");
        ThrowUtils.throwIf(ObjectUtils.isEmpty(fileContent), ErrorCode.PARAMS_ERROR, "文件内容不能为空");
        StringBuilder input = new StringBuilder()
                .append("必须严格按照如下模板生成相应的内容：\n" +
                        "【【标志】】\n" +
                        "## 代码注释：\n" +
                        "```\n" +
                        "生成注释后的代码内容\n" +
                        "```\n" +
                        "【【标志】】\n" +
                        "## 使用语言：\n" +
                        "生成内容\n" +
                        "【【标志】】\n" +
                        "## 代码解释\n" +
                        "生成的内容\n" +
                        "【【标志】】\n" +
                        "## 代码规范\n" +
                        "### 命名规范：\n" +
                        "生成内容。\n" +
                        "### 代码格式：\n" +
                        "生成内容。\n" +
                        "### 代码复用：\n" +
                        "生成内容。\n" +
                        "### 错误处理：\n" +
                        "生成内容。 \n" +
                        "### 安全性：\n" +
                        "代生成内容。\n" +
                        "### 代码复杂度：\n" +
                        "生成内容。\n" +
                        "### 评分理由： \n" +
                        "- 命名规范：理由：得分。 \n" +
                        "- 代码格式：理由：得分。\n" +
                        "- 代码复用：理由：得分。 \n" +
                        "- 错误处理：理由：得分。 \n" +
                        "- 安全性：理由：得分。 \n" +
                        "- 代码复杂度：理由：得分。 \n" +
                        "### 雷达图\n" +
                        "【【标志】】\n" +
                        "```\n" +
                        "option = {\n" +
                        "  title: {\n" +
                        "    text: 'Basic Radar Chart'\n" +
                        "  },\n" +
                        "  legend: {\n" +
                        "    data: ['Allocated Budget', 'Actual Spending']\n" +
                        "  },\n" +
                        "  radar: {\n" +
                        "    // shape: 'circle',\n" +
                        "    indicator: [\n" +
                        "      { name: 'Sales', max: 6500 },\n" +
                        "      { name: 'Administration', max: 16000 },\n" +
                        "      { name: 'Information Technology', max: 30000 },\n" +
                        "      { name: 'Customer Support', max: 38000 },\n" +
                        "      { name: 'Development', max: 52000 },\n" +
                        "      { name: 'Marketing', max: 25000 }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  series: [\n" +
                        "    {\n" +
                        "      name: 'Budget vs spending',\n" +
                        "      type: 'radar',\n" +
                        "      data: [\n" +
                        "        {\n" +
                        "          value: [4200, 3000, 20000, 35000, 50000, 18000],\n" +
                        "          name: 'Allocated Budget'\n" +
                        "        },\n" +
                        "        {\n" +
                        "          value: [5000, 14000, 28000, 26000, 42000, 21000],\n" +
                        "          name: 'Actual Spending'\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "};\n" +
                        "```\n" +
                        "【【标志】】\n" +
                        "## 优化建议\n" +
                        "生成的内容\n" +
                        "【【标志】】\n" +
                        "模板内容结束！\n");

        StringBuilder input1 = new StringBuilder()
                .append("分析要求：\n")
                .append("1. 需要在代码的每一行添加注释，如果代码有效长度大于100行，则只对代码的重点部分进行注释生成，并且只展示重点难点部分，生成注释后的代码内容不得大于80行。\n" +
                        "2. 检查代码规范程度，包含以下检查规则: \n" +
                        "\t1. 命名规范：检查变量、函数、类、模块等的命名是否符合既定的命名约定 。\n" +
                        "\t2. 代码格式：包括缩进、空格、换行等，确保代码具有良好的视觉效果和一致的格式风格 。\n" +
                        "\t3. 代码复用：检查函数、类和模块的复用，避免重复代码，提高代码的可维护性 。\n" +
                        "\t4. 错误处理：检查错误处理机制是否得当，确保程序在出现异常时能够给出清晰的错误信息，并且能够合理地恢复或终止。\n" +
                        "\t5. 安全性：检查代码中可能存在的安全漏洞，如SQL注入、跨站脚本攻击（XSS）、不安全的API调用等。 \n" +
                        "\t6. 代码复杂度：避免过度复杂的逻辑，保持代码简单、易于理解。 \n" +
                        "\t\n" +
                        "\t根据规范成度分析结果给出评分，语法为markdown。\n" +
                        "\t需要输出符合echarts语法规范雷达图类型的代码，注意需要根据你的评分进行雷达图生成，并且给出评分理由，满分为10分。\n" +
                        "3. 说明代码使用了什么语言，可能是是哪个技术框架下的代码\n" +
                        "4. 说明解释此代码，是什么，可能有什么用等\n" +
                        "5. 给出代码优化建议，需要详细全面")
                .append("这里是需要你分析的代码：\n")
                .append(fileContent);
//        Double token = moonlightAPI.token(SYSTEM_PRESETS, input.toString());
        String response = moonlightAPI.chat(input.toString(), input1.toString());
        List<String> strings = extractFlagsContent(response);
//        strings.add(token.toString());
        // 移除生成结果中的多余符号
        return strings;
    }

    @Override
    public List<String> filesAIGC(String goal, String fileContent) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(goal), ErrorCode.PARAMS_ERROR, "分析需求不能为空");
        ThrowUtils.throwIf(ObjectUtils.isEmpty(fileContent), ErrorCode.PARAMS_ERROR, "文件内容不能为空");
        StringBuilder input = new StringBuilder()
                .append("必须严格按照如下模板生成相应的内容，需要注意的是代码只是示例你需要根据要求自己生成对应的代码：\n" +
                        "【【标志】】\n" +
                        "## 项目简介\n" +
                        "生成的内容\n" +
                        "【【标志】】\n" +
                        "## 目录树状图：\n" +
                        "```\n" +
                        "myChart.showLoading();\n" +
                        "$.get(ROOT_PATH + '/data/asset/data/flare.json', function (data) {\n" +
                        "  myChart.hideLoading();\n" +
                        "\n" +
                        "  data.children.forEach(function (\n" +
                        "    datum: { collapsed: boolean },\n" +
                        "    index: number\n" +
                        "  ) {\n" +
                        "    index % 2 === 0 && (datum.collapsed = true);\n" +
                        "  });\n" +
                        "\n" +
                        "  myChart.setOption(\n" +
                        "    (option = {\n" +
                        "      tooltip: {\n" +
                        "        trigger: 'item',\n" +
                        "        triggerOn: 'mousemove'\n" +
                        "      },\n" +
                        "      series: [\n" +
                        "        {\n" +
                        "          type: 'tree',\n" +
                        "\n" +
                        "          data: [data],\n" +
                        "\n" +
                        "          top: '1%',\n" +
                        "          left: '7%',\n" +
                        "          bottom: '1%',\n" +
                        "          right: '20%',\n" +
                        "\n" +
                        "          symbolSize: 7,\n" +
                        "\n" +
                        "          label: {\n" +
                        "            position: 'left',\n" +
                        "            verticalAlign: 'middle',\n" +
                        "            align: 'right',\n" +
                        "            fontSize: 9\n" +
                        "          },\n" +
                        "\n" +
                        "          leaves: {\n" +
                        "            label: {\n" +
                        "              position: 'right',\n" +
                        "              verticalAlign: 'middle',\n" +
                        "              align: 'left'\n" +
                        "            }\n" +
                        "          },\n" +
                        "\n" +
                        "          emphasis: {\n" +
                        "            focus: 'descendant'\n" +
                        "          },\n" +
                        "\n" +
                        "          expandAndCollapse: true,\n" +
                        "          animationDuration: 550,\n" +
                        "          animationDurationUpdate: 750\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    })\n" +
                        "  );\n" +
                        "});\n" +
                        "```\n" +
                        "【【标志】】\n" +
                        "## 项目技术栈饼状图：\n" +
                        "```\n" +
                        "option = {\n" +
                        "  tooltip: {\n" +
                        "    trigger: 'item'\n" +
                        "  },\n" +
                        "  legend: {\n" +
                        "    top: '5%',\n" +
                        "    left: 'center'\n" +
                        "  },\n" +
                        "  series: [\n" +
                        "    {\n" +
                        "      name: 'Access From',\n" +
                        "      type: 'pie',\n" +
                        "      radius: ['40%', '70%'],\n" +
                        "      avoidLabelOverlap: false,\n" +
                        "      itemStyle: {\n" +
                        "        borderRadius: 10,\n" +
                        "        borderColor: '#fff',\n" +
                        "        borderWidth: 2\n" +
                        "      },\n" +
                        "      label: {\n" +
                        "        show: false,\n" +
                        "        position: 'center'\n" +
                        "      },\n" +
                        "      emphasis: {\n" +
                        "        label: {\n" +
                        "          show: true,\n" +
                        "          fontSize: 40,\n" +
                        "          fontWeight: 'bold'\n" +
                        "        }\n" +
                        "      },\n" +
                        "      labelLine: {\n" +
                        "        show: false\n" +
                        "      },\n" +
                        "      data: [\n" +
                        "        { value: 1048, name: 'Search Engine' },\n" +
                        "        { value: 735, name: 'Direct' },\n" +
                        "        { value: 580, name: 'Email' },\n" +
                        "        { value: 484, name: 'Union Ads' },\n" +
                        "        { value: 300, name: 'Video Ads' }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "};\n" +
                        "```\n" +
                        "【【标志】】\n" +
                        "## 如何运行：\n" +
                        "生成内容\n" +
                        "【【标志】】\n" +
                        "## 实体关系图 \n" +
                        "```\n" +
                        "myChart.showLoading();\n" +
                        "$.getJSON(ROOT_PATH + '/data/asset/data/les-miserables.json', function (graph) {\n" +
                        "  myChart.hideLoading();\n" +
                        "\n" +
                        "  option = {\n" +
                        "    tooltip: {},\n" +
                        "    legend: [\n" +
                        "      {\n" +
                        "        data: graph.categories.map(function (a: { name: string }) {\n" +
                        "          return a.name;\n" +
                        "        })\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    series: [\n" +
                        "      {\n" +
                        "        name: 'Les Miserables',\n" +
                        "        type: 'graph',\n" +
                        "        layout: 'none',\n" +
                        "        data: graph.nodes,\n" +
                        "        links: graph.links,\n" +
                        "        categories: graph.categories,\n" +
                        "        roam: true,\n" +
                        "        label: {\n" +
                        "          show: true,\n" +
                        "          position: 'right',\n" +
                        "          formatter: '{b}'\n" +
                        "        },\n" +
                        "        labelLayout: {\n" +
                        "          hideOverlap: true\n" +
                        "        },\n" +
                        "        scaleLimit: {\n" +
                        "          min: 0.4,\n" +
                        "          max: 2\n" +
                        "        },\n" +
                        "        lineStyle: {\n" +
                        "          color: 'source',\n" +
                        "          curveness: 0.3\n" +
                        "        }\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  };\n" +
                        "\n" +
                        "  myChart.setOption(option);\n" +
                        "});\n" +
                        "```\n" +
                        "【【标志】】\n" +
                        "## 第三方api：\n" +
                        "生成内容\n" +
                        "【【标志】】\n" +
                        "## 代码规范\n" +
                        "### 命名规范：\n" +
                        "生成内容。\n" +
                        "### 代码格式：\n" +
                        "生成内容。\n" +
                        "### 代码复用：\n" +
                        "生成内容。\n" +
                        "### 错误处理：\n" +
                        "生成内容。 \n" +
                        "### 安全性：\n" +
                        "代生成内容。\n" +
                        "### 代码复杂度：\n" +
                        "生成内容。\n" +
                        "### 评分理由： \n" +
                        "- 命名规范：理由：得分。 \n" +
                        "- 代码格式：理由：得分。\n" +
                        "- 代码复用：理由：得分。 \n" +
                        "- 错误处理：理由：得分。 \n" +
                        "- 安全性：理由：得分。 \n" +
                        "- 代码复杂度：理由：得分。 \n" +
                        "### 雷达图\n" +
                        "【【标志】】\n" +
                        "```\n" +
                        "option = {\n" +
                        "  title: {\n" +
                        "    text: 'Basic Radar Chart'\n" +
                        "  },\n" +
                        "  legend: {\n" +
                        "    data: ['Allocated Budget', 'Actual Spending']\n" +
                        "  },\n" +
                        "  radar: {\n" +
                        "    // shape: 'circle',\n" +
                        "    indicator: [\n" +
                        "      { name: 'Sales', max: 6500 },\n" +
                        "      { name: 'Administration', max: 16000 },\n" +
                        "      { name: 'Information Technology', max: 30000 },\n" +
                        "      { name: 'Customer Support', max: 38000 },\n" +
                        "      { name: 'Development', max: 52000 },\n" +
                        "      { name: 'Marketing', max: 25000 }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  series: [\n" +
                        "    {\n" +
                        "      name: 'Budget vs spending',\n" +
                        "      type: 'radar',\n" +
                        "      data: [\n" +
                        "        {\n" +
                        "          value: [4200, 3000, 20000, 35000, 50000, 18000],\n" +
                        "          name: 'Allocated Budget'\n" +
                        "        },\n" +
                        "        {\n" +
                        "          value: [5000, 14000, 28000, 26000, 42000, 21000],\n" +
                        "          name: 'Actual Spending'\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "};\n" +
                        "```\n" +
                        "【【标志】】\n" +
                        "## 优化建议\n" +
                        "生成的内容\n" +
                        "【【标志】】\n" +
                        "模板内容结束！");
        StringBuilder input1 = new StringBuilder()
                .append("分析要求：\n")
                .append("1. 项目简介：说明解释此代码，是什么，可能有什么用等\n" +
                        "2. 项目结构：根据项目给出目录，如果项目没有完整目录或者其他原因，那就假设这个项目在com.ex目录下，由文件名称构建出目录，由这个目录输出符合echarts语法规则的树状图的代码\n" +
                        "3. 项目技术栈，分析这个代码各个技术的占比，给出对应技术的的百分数比例，再由分析后的占比的数据，输出符合echarts语法规饼状图代码，注意需要根据你分析后的各个技术的占比来进行饼状图生成\n" +
                        "4. 如果项目可以运行，详细分步骤描述如何运行\n" +
                        "5. 分析项目实体，根据实体关系分析内容输出符合echarts语法规则的关系图的代码\n" +
                        "6. 如果有第三方接口，就分点描述调用了哪些第三方接口，如果没有就说：暂未发现第三方api接口\n" +
                        "7. 检查代码规范程度，包含以下检查规则: \n" +
                        "\t1. 命名规范：检查变量、函数、类、模块等的命名是否符合既定的命名约定 。\n" +
                        "\t2. 代码格式：包括缩进、空格、换行等，确保代码具有良好的视觉效果和一致的格式风格 。\n" +
                        "\t3. 代码复用：检查函数、类和模块的复用，避免重复代码，提高代码的可维护性 。\n" +
                        "\t4. 错误处理：检查错误处理机制是否得当，确保程序在出现异常时能够给出清晰的错误信息，并且能够合理地恢复或终止。\n" +
                        "\t5. 安全性：检查代码中可能存在的安全漏洞，如SQL注入、跨站脚本攻击（XSS）、不安全的API调用等。 \n" +
                        "\t6. 代码复杂度：避免过度复杂的逻辑，保持代码简单、易于理解。 \n" +
                        "\t\n" +
                        "\t根据规范成度分析结果给出评分，语法为markdown。\n" +
                        "\t需要输出符合echarts语法规范雷达图类型的代码，注意需要根据你的评分进行雷达图生成，并且给出评分理由，满分为10分。\n" +
                        "8. 给出代码优化建议，需要详细全面")
                .append("分析内容如下：")
                .append(fileContent);
        StringBuilder input2 = new StringBuilder()
                .append("分析要求：\n")
                .append("7. 检查代码规范程度，包含以下检查规则: \n" +
                        "\t1. 命名规范：检查变量、函数、类、模块等的命名是否符合既定的命名约定 。\n" +
                        "\t2. 代码格式：包括缩进、空格、换行等，确保代码具有良好的视觉效果和一致的格式风格 。\n" +
                        "\t3. 代码复用：检查函数、类和模块的复用，避免重复代码，提高代码的可维护性 。\n" +
                        "\t4. 错误处理：检查错误处理机制是否得当，确保程序在出现异常时能够给出清晰的错误信息，并且能够合理地恢复或终止。\n" +
                        "\t5. 安全性：检查代码中可能存在的安全漏洞，如SQL注入、跨站脚本攻击（XSS）、不安全的API调用等。 \n" +
                        "\t6. 代码复杂度：避免过度复杂的逻辑，保持代码简单、易于理解。 \n" +
                        "\t\n" +
                        "\t根据规范成度分析结果给出评分，语法为markdown。\n" +
                        "\t需要输出符合echarts语法规范雷达图类型的代码，注意需要根据你的评分进行雷达图生成，并且给出评分理由，满分为10分。\n" +
                        "8. 给出代码优化建议，需要详细全面")
                .append("分析内容如下：")
                .append(fileContent);
//        Double token = moonlightAPI.token(SYSTEM_PRESETS, input.toString());
        String response1 = moonlightAPI.chat(input.toString(), input1.toString());

        String response2 = moonlightAPI.chat(input.toString(), input2.toString());
        List<String> strings1 = extractFlagsContent(response1);
        List<String> strings2 = extractFlagsContent(response2);
        List<String> strings = new ArrayList<>();
        strings.addAll(strings1);
        strings.addAll(strings2);
//        strings.add(token.toString());
        // 移除生成结果中的多余符号
        return strings;
    }

    public String codeExtractor(String code){
        Pattern pattern = Pattern.compile("```(.*?)```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            String radarChartCode = matcher.group(1).trim();
            return radarChartCode;
        } else {
            return null;
        }
    }
    public List<String> extractFlagsContent(String text) {
    Pattern pattern = Pattern.compile("(?s)【【标志】】(.*?)(?=【【标志】】|$)");
    Matcher matcher = pattern.matcher(text);

    List<String> contents = new ArrayList<>();
    while (matcher.find()) {
        String extractedContent = matcher.group(1); // 只获取括号内匹配的内容（即去掉【【标志】】）
        contents.add(extractedContent);
    }
    return contents;
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
        queryWrapper.eq(id != null && id > 0, "id", id); // 如果id存在且大于0，则添加id的等于条件
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
