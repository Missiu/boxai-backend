package com.boxai.common.constants;

/**
 * 数据清洗常量接口,用于代码的自动化数据清洗过程。
 */
public interface DataCleanConstant {
    /**
     * 移除多余空白行
     */
    String REMOVE_EXTRA_BLANK_LINE = "(?:(\\r\\n)|\\n){2,}";
    /**
     * 移除多余导入
     */
    String REMOVE_EXTRA_IMPORT = "import\\s+[^;]+;";
    /**
     * 获取代码内容
     */
    String GET_CODE_CONTENT = "```[a-z]*\\s*([\\s\\S]*?)\\s*```";
    /**
     * 匹配"【【标志】】"之间
     */
    String GET_FLAG_CONTENT = "【【[^】]*】】([^【]*)";
    /**
     * 匹配一个或多个连续的数字
     */
    String MATCH_NUMBER = "\\D+";
}
