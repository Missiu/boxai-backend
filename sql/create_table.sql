-- 创建库
CREATE DATABASE IF NOT EXISTS boxai_db;

-- 切换库
USE boxai_db;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id           BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userAccount  VARCHAR(256) UNIQUE NOT NULL COMMENT '账号',
    userPassword VARCHAR(256) NOT NULL COMMENT '密码',
    userName     VARCHAR(256)   default 'boxai_user'                                                                         NOT  NULL     COMMENT '用户昵称',
    userAvatar   VARCHAR(1024) DEFAULT 'https://photokit.com/features/images/image-text-after.webp'                        NULL     COMMENT '用户头像',
    userProfile  VARCHAR(1024)                          NULL     COMMENT '用户简介',
    availableBalance  DECIMAL(20, 2)                          NULL     COMMENT '可用余额',
    voucherBalance  DECIMAL(20, 2)                          NULL     COMMENT '代金券余额, 不会为负数',
    cashBalance  DECIMAL(20, 2)                          NULL     COMMENT '现金余额, 可能为负数, 代表用户欠费',
    userRole     VARCHAR(256) DEFAULT 'user'            NOT NULL COMMENT '用户角色：user/vip',
    createTime   DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete     TINYINT       DEFAULT 0                NOT NULL COMMENT '是否删除'
) COMMENT '用户' COLLATE = utf8mb4_unicode_ci;
-- 数据表 result
CREATE TABLE IF NOT EXISTS result (
   -- 主键与基础信息
    id BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    goal TEXT NULL COMMENT '目标',
    genName VARCHAR(255) NULL COMMENT '分析的名称',
    createTime   DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete BOOLEAN DEFAULT FALSE NOT NULL COMMENT '是否逻辑删除',
    usedToken    VARCHAR(1024)                          NULL     COMMENT 'ai使用量',
    userId BIGINT NOT NULL COMMENT '用户id',
    codeComment TEXT NULL COMMENT '生成的代码注释',
    rawData MEDIUMTEXT NULL COMMENT '原始数据',
    codeProfile TEXT NULL COMMENT '生成的代码简介',
    codeEntity TEXT NULL COMMENT '生成的代码实体',
    codeAPI TEXT NULL COMMENT '生成的代码API',
    codeRun TEXT NULL COMMENT '生成的代码运行',
    codeSuggestion TEXT NULL COMMENT '生成的代码建议',
    codeNorm TEXT NULL COMMENT '代码规范-雷达图',
    codeNormStr TEXT NULL COMMENT '代码规范-雷达图说明',
    codeTechnology TEXT NULL COMMENT '代码技术-饼状图',
    codeCataloguePath TEXT NULL COMMENT '代码目录-树状图'
) COMMENT '数据信息表' COLLATE = utf8mb4_unicode_ci;

-- 帖子分享表
create table if not exists post
(
    id           bigint auto_increment comment 'id' primary key,
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                  not null comment '收藏数',
    content  TEXT                               null comment '分享描述内容',
    userId       bigint                              not null comment '创建用户 id',
    resultId       bigint                              not null comment '帖子 id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId),
    index idx_resultId (resultId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    resultId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_resultId (resultId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    resultId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_resultId (resultId),
    index idx_userId (userId)
) comment '帖子收藏';

-- 帖子评论表
CREATE TABLE IF NOT EXISTS post_comment
(
    id         BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    resultId     BIGINT                             NOT NULL COMMENT '帖子 id',
    userId     BIGINT                             NOT NULL COMMENT '创建用户 id',
    content    TEXT                              NOT NULL COMMENT '评论内容',
    createTime datetime DEFAULT CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime DEFAULT CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    FOREIGN KEY (userId) REFERENCES user(id),
    FOREIGN KEY (resultId) REFERENCES result(id),
    index idx_resultId (resultId),
    index idx_userId (userId)
) comment '帖子评论';