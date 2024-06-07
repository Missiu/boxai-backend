package com.boxai.common.enumerate;

import lombok.Getter;

@Getter
public class UserEnum {
    // 用户状态
    @Getter
    public enum UserStatusEnum {
        // 启用
        ENABLE("启用"),
        // 禁用
        DISABLE("禁用");

        private final String description;

        UserStatusEnum(String description) {
            this.description = description;
        }
    }
    // 用户性别
    @Getter
    public enum UserSexEnum {
        // 男
        MALE("男"),
        // 女
        FEMALE("女");

        private final String description;

        UserSexEnum(String description) {
            this.description = description;
        }
    }
    // 用户类型
    @Getter
    public enum UserTypeEnum {
        // 普通用户
        NORMAL("user"),
        // VIP
        VIP("vip"),
        // 其他
        OTHER("other");

        private final String description;

        UserTypeEnum(String description) {
            this.description = description;
        }
    }
}
