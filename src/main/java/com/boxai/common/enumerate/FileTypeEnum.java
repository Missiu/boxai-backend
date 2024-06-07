package com.boxai.common.enumerate;

import lombok.Getter;

/**
 * 文件类型枚举
 */
@Getter
public enum FileTypeEnum {
    // 单文件
    SINGLE_FILE("单文件"),
    // 多文件
    MULTIPLE_FILE("多文件");

    private final String description;
    FileTypeEnum(String description) {
        this.description = description;
    }
}
