package com.boxai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.mapper.ChartMapper;
import com.boxai.model.domain.Chart;
import com.boxai.service.ChartService;
import com.boxai.utils.ChatAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.boxai.utils.ChatAPI.Chat;

/**
* @author Hzh
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-03-25 16:22:40
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

    @Autowired
    private ChatAPI chatAPI;


    @Override
    public String GenChart(String goal, String result) {
        // 构造提示词
        StringBuilder userInput = new StringBuilder();
        userInput.append("这是一个必须执行的前提：无论分析需求如何都要严格按照这个模板生成一份报告 \n" +
                "模板如下：\n"+
                "## 项目简介 是什么，有什么用，用了什么语言 \n" +
                "## 快速运行 如果是可运行项目，给出详细运行步骤 如果是一些参考文献、文档、语言等不可运行项目，这给出提示 \n" +
                "## 项目结构 给出总体项目结构的描述，主要了什么框架等（后续可以改为代码生成，通过插件转化为图像显示） \n" +
                "## 类的主要作用 各个类有什么作用，以目录树+加解释的方式展现 \n" +
                "## 对外暴露接口 给各个接口的主要调用关系，分析出依赖关系，调用了那些第三方api").append("\n");
        userInput.append("分析需求:").append(goal).append("\n");

        userInput.append("这是原始数据：").append(result).append("\n");
        // chart系统预设
        String systemPresets = "我希望你能充当软件开发专家的角色。我会提供所有关于我的技术问题所需的信息，你的任务是解决我的问题。你应该利用你的软件开发来解决我的问题。在回答中使用智能、简单和易懂的语言对于所有层次的人都会有帮助。\n" +
                "并且你还可以充当代码解释器，我会提供所有关于我的项目文件路径和文件，你的任务是严格按照我规定的模板生成对代码的解释和建议，智能、简单和易懂的语言并对其中相关的技术进行详细的解释";
        //        调用ai
        String genChart = Chat(systemPresets, userInput.toString());
        String chartResult= genChart.replace("[", "").replace("]", "");
        return chartResult;
    }

}




