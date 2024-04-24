package com.boxai.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ResultServiceImplTest {

    @Autowired
    private ResultServiceImpl chartService;
    @Test
    void genChart() {
        chartService.fileAIGC("这是什么代码", "package com.boxai.api;\n" +
                "\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.boot.test.context.SpringBootTest;\n" +
                "\n" +
                "import static org.junit.jupiter.api.Assertions.*;\n" +
                "\n" +
                "@SpringBootTest\n" +
                "class MoonlightAPITest {\n" +
                "    @Autowired\n" +
                "    private MoonlightAPI moonlightAPI;\n" +
                "\n" +
                "    @Test\n" +
                "    void chat() {\n" +
                "        System.out.println(moonlightAPI.chat(\"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\", \"你好，我叫李雷，1+1等于多少？\"));\n" +
                "    }\n" +
                "\n" +
                "    @Test\n" +
                "    void token() {\n" +
                "        System.out.println(moonlightAPI.token(\"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\",\"你好，我叫李雷，1+1等于多少？\"));\n" +
                "    }\n" +
                "    @Test\n" +
                "    void fetchBalance(){\n" +
                "        System.out.println(moonlightAPI.fetchBalance());\n" +
                "    }\n" +
                "}");
    }
    @Test
    void codeExtractor(){
        String description = "这是一段描述\n" +
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
                "```";
        System.out.println(chartService.codeExtractor(description));
    }
    @Test
    void extractFlagsContent() {
        List<String> strings = chartService.extractFlagsContent("【【标志】】\n" +
                "## 代码注释：\n" +
                "```java\n" +
                "package com.boxai.model.dto.aigc;\n" +
                "\n" +
                "import lombok.Data;\n" +
                "\n" +
                "import java.io.Serial;\n" +
                "import java.io.Serializable;\n" +
                "\n" +
                "/**\n" +
                " * 通过AI生成图表的请求参数类\n" +
                " * 用于封装AI生成图表时所需要的请求数据\n" +
                " * @author Hzh\n" +
                " */\n" +
                "@Data\n" +
                "public class ChartGenRequest implements Serializable {\n" +
                "\n" +
                "    @Serial\n" +
                "    private static final long serialVersionUID = 1783025723868066981L;\n" +
                "\n" +
                "    // 用于指定AI需要分析的数据的名称\n" +
                "    private String genName;\n" +
                "\n" +
                "    // 用于指定AI进行分析时的目标，例如：趋势分析、对比分析等\n" +
                "    private String goal;\n" +
                "}\n" +
                "```\n" +
                "【【标志】】\n" +
                "## 使用语言：\n" +
                "Java\n" +
                "\n" +
                "【【标志】】\n" +
                "## 代码解释\n" +
                "这段代码定义了一个名为`ChartGenRequest`的类，它位于`com.boxai.model.dto.aigc`包下。这个类使用了`lombok`库的`@Data`注解来自动生成getter和setter方法，实现了`Serializable`接口，用于序列化。类中有两个私有成员变量：`genName`和`goal`，分别用于存储AI分析数据的名称和分析目标。\n" +
                "\n" +
                "【【标志】】\n" +
                "## 代码规范\n" +
                "### 命名规范：\n" +
                "生成内容。变量和类的命名遵循了Java的命名约定，使用了驼峰命名法，并且变量名具有描述性。\n" +
                "\n" +
                "### 代码格式：\n" +
                "生成内容。代码格式良好，使用了正确的缩进和空格。\n" +
                "\n" +
                "### 代码复用：\n" +
                "生成内容。由于代码只是一个简单的数据传输对象(DTO)，没有明显的函数或类复用。\n" +
                "\n" +
                "### 错误处理：\n" +
                "生成内容。代码中没有错误处理的逻辑，因为这是一个简单的数据类。\n" +
                "\n" +
                "### 安全性：\n" +
                "代生成内容。代码中没有明显的安全漏洞，但是作为一个请求参数类，应当确保输入数据的验证和过滤。\n" +
                "\n" +
                "### 代码复杂度：\n" +
                "生成内容。代码非常简单，没有复杂的逻辑。\n" +
                "\n" +
                "### 评分理由： \n" +
                "- 命名规范：命名清晰，符合Java标准，得分10。\n" +
                "- 代码格式：格式规范，得分10。\n" +
                "- 代码复用：由于是简单的数据类，得分8。\n" +
                "- 错误处理：没有错误处理，得分5。\n" +
                "- 安全性：没有明显漏洞，但需注意输入验证，得分8。\n" +
                "- 代码复杂度：非常简单，得分10。\n" +
                "\n" +
                "### 雷达图\n" +
                "根据评分，生成雷达图的代码如下：\n" +
                "\n" +
                "```javascript\n" +
                "option = {\n" +
                "  title: {\n" +
                "    text: 'Code Quality Radar Chart'\n" +
                "  },\n" +
                "  legend: {\n" +
                "    data: ['Naming Convention', 'Code Format', 'Code Reusability', 'Error Handling', 'Security', 'Code Complexity']\n" +
                "  },\n" +
                "  radar: {\n" +
                "    indicator: [\n" +
                "      { name: 'Naming Convention', max: 10 },\n" +
                "      { name: 'Code Format', max: 10 },\n" +
                "      { name: 'Code Reusability', max: 10 },\n" +
                "      { name: 'Error Handling', max: 10 },\n" +
                "      { name: 'Security', max: 10 },\n" +
                "      { name: 'Code Complexity', max: 10 }\n" +
                "    ]\n" +
                "  },\n" +
                "  series: [{\n" +
                "    name: 'Code Quality',\n" +
                "    type: 'radar',\n" +
                "    data : [\n" +
                "      {\n" +
                "        value : [10, 10, 8, 5, 8, 10],\n" +
                "        name : 'Scores'\n" +
                "      }\n" +
                "    ]\n" +
                "  }]\n" +
                "};\n" +
                "```\n" +
                "\n" +
                "【【标志】】\n" +
                "## 优化建议\n" +
                "生成的内容：\n" +
                "1. 对于`genName`和`goal`，可以增加数据验证，确保传入的数据格式正确。\n" +
                "2. 如果`ChartGenRequest`类将被频繁序列化或反序列化，考虑优化`serialVersionUID`的生成方式。\n" +
                "3. 考虑使用更具体的类名，如`AIChartGenerationRequest`，以提供更多的上下文信息。\n" +
                "4. 如果这个类将被用在多线程环境中，考虑线程安全问题。\n" +
                "5. 如果`goal`字段有预定义的值，可以考虑使用枚举类型来限制可能的值。");
        for (String s: strings) {
            System.out.println("内容："+s);
        }
    }

    @Test
    void getQueryWrapper() {
    }
}