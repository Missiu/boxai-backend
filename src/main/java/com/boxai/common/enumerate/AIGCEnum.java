package com.boxai.common.enumerate;

import lombok.Getter;

@Getter
public enum AIGCEnum {
    // 排队中
    QUEUEING("排队中"),
    // 执行中
    EXECUTING("执行中"),
    // 已完成
    COMPLETED("已完成"),
    // 失败
    FAILED("失败"),

    USER("user"),

    SYSTEM("system");

    private final String description;

    AIGCEnum(String description) {
        this.description = description;
    }
    @Getter
    public enum CodeKey {
        CODE_COMMENTS("codeComments", "代码注释"),
        CODE_PROFILE_DESCRIPTION("codeProfileDescription", "代码简介描述"),
        CODE_ENTITIES("codeEntities", "代码实体关系图"),
        CODE_APIS("codeApis", "代码APIs"),
        CODE_EXECUTION("codeExecution", "代码执行"),
        CODE_NORM_RADAR_DESCRIPTION("codeNormRadarDescription", "代码规范雷达描述"),
        CODE_NORM_RADAR("codeNormRadar", "代码规范雷达图"),
        CODE_TECHNOLOGY_PIE("codeTechnologyPie", "代码技术饼图"),
        CODE_CATALOG_PATH("codeCatalogPath", "代码目录路径"),
        CODE_SUGGESTIONS("codeSuggestions", "代码建议"),
        AI_TOKEN_USAGE("aiTokenUsage", "消耗token");

        private final String key;
        private final String description;

        CodeKey(String key, String description) {
            this.key = key;
            this.description = description;
        }

        public String getKey() {
            return key;
        }

        public String getDescription() {
            return description;
        }
    }
}

