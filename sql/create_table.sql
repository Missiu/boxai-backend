-- 创建库
CREATE DATABASE IF NOT EXISTS boxai_db;

-- 切换库
USE boxai_db;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
                                    id           BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
                                    userAccount  VARCHAR(256)                           NOT NULL COMMENT '账号',
                                    userPassword VARCHAR(512)                           NOT NULL COMMENT '密码',
                                    userName     VARCHAR(256)                           NULL     COMMENT '用户昵称',
                                    userAvatar   VARCHAR(1024)                          NULL     COMMENT '用户头像',
                                    userProfile  VARCHAR(1024)                          NULL     COMMENT '用户简介',
                                    usedToken    VARCHAR(1024)                          NULL     COMMENT 'ai使用量',
                                    Token        VARCHAR(1024)                          NULL     COMMENT '总量',
                                    userRole     VARCHAR(256) DEFAULT 'user'            NOT NULL COMMENT '用户角色：user/admin',
                                    createTime   DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
                                    updateTime   DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    isDelete     TINYINT       DEFAULT 0                NOT NULL COMMENT '是否删除',
                                    INDEX idx_userAccount (userAccount)
) COMMENT '用户' COLLATE = utf8mb4_unicode_ci;

-- 图表信息表
CREATE TABLE IF NOT EXISTS chart (
                                     id           BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
                                     genName      VARCHAR(128)                            NULL     COMMENT '分析数据的名称',
                                     goal         TEXT                                   NULL     COMMENT '分析目标',
                                     chatData     MEDIUMTEXT                             NULL     COMMENT '原始数据',
                                     genChart     TEXT                                   NULL     COMMENT '生成的代码数据(备用)',
                                     genResult    TEXT                                   NULL     COMMENT '生成分析结论',
                                     userId       BIGINT                                 NOT NULL COMMENT '创建的用户id',
                                     createTime   DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
                                     updateTime   DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     isDelete     TINYINT       DEFAULT 0                NOT NULL COMMENT '是否删除',
                                     FOREIGN KEY (userId) REFERENCES user(id) ON DELETE CASCADE
) COMMENT '数据信息表' COLLATE = utf8mb4_unicode_ci;
