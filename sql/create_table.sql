-- 创建库
CREATE DATABASE IF NOT EXISTS boxai
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
-- 切换库
USE boxai;

-- 用户表 users
CREATE TABLE IF NOT EXISTS users
(
    id                BIGINT AUTO_INCREMENT COMMENT '用户ID' PRIMARY KEY,
    account           VARCHAR(64) UNIQUE                                                                  NOT NULL COMMENT '账号',
    password_hash     VARCHAR(255)                                                                        NOT NULL COMMENT '密码散列值', -- bcrypt散列长度示例
    nickname          VARCHAR(50)    DEFAULT 'box_ai_user'                                                NOT NULL COMMENT '用户昵称',
    avatar_url        VARCHAR(255)   DEFAULT 'https://photokit.com/features/images/image-text-after.webp' NULL COMMENT '用户头像',
    profile           VARCHAR(100)   DEFAULT '暂无个人简介'                                               NULL COMMENT '用户简介',
    available_balance DECIMAL(10, 2) DEFAULT 0.00                                                         NULL COMMENT '可用余额',
    voucher_balance   DECIMAL(10, 2) DEFAULT 0.00                                                         NULL COMMENT '代金券余额',
    cash_balance      DECIMAL(10, 2) DEFAULT 0.00                                                         NULL COMMENT '现金余额',
    role              VARCHAR(255)   DEFAULT 'user'                                                       NOT NULL COMMENT '用户角色/user/vip/自定义key',
    create_time       DATETIME       DEFAULT CURRENT_TIMESTAMP                                            NOT NULL COMMENT '创建时间',
    update_time       DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP                NOT NULL COMMENT '更新时间',
    is_delete         TINYINT(1)     DEFAULT FALSE                                                        NOT NULL COMMENT '是否删除',
    INDEX idx_users_id_is_delete (id, is_delete),
    INDEX idx_users_account (account)
) COMMENT ='用户信息表';

-- 修改分隔符
DELIMITER //

-- 创建触发器
CREATE TRIGGER set_default_nickname
    BEFORE INSERT
    ON users
    FOR EACH ROW
BEGIN
    DECLARE new_id INT;
    -- 获取下一个自增的ID
    SELECT AUTO_INCREMENT
    INTO new_id
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'users';

    -- 设置新的nickname值
    SET NEW.nickname = CONCAT('box_ai_user', new_id);
END;
//

-- 恢复分隔符为默认值
DELIMITER ;

-- 数据表 data_charts
CREATE TABLE IF NOT EXISTS data_charts
(
    id                          BIGINT AUTO_INCREMENT COMMENT '结果ID' PRIMARY KEY,
    goal_description            VARCHAR(255)                                                       NULL COMMENT '目标描述',
    generation_name             VARCHAR(100)                                                       NULL COMMENT '生成名称',
    ai_token_usage              INT UNSIGNED DEFAULT 0                                             NULL COMMENT 'AI使用量',
    user_id                     BIGINT                                                             NOT NULL COMMENT '用户ID',
    raw_data                    TEXT                                                               NULL COMMENT '原始数据',
    code_comments               TEXT                                                               NULL COMMENT '代码注释',
    code_profile_description    TEXT                                                               NULL COMMENT '代码简介',
    code_entities               TEXT                                                               NULL COMMENT '代码实体',
    code_apis                   TEXT                                                               NULL COMMENT '代码API',
    code_execution              TEXT                                                               NULL COMMENT '代码执行',
    code_suggestions            TEXT                                                               NULL COMMENT '代码建议',
    code_norm_radar             TEXT                                                               NULL COMMENT '代码规范-雷达图',
    code_norm_radar_description TEXT                                                               NULL COMMENT '代码规范-雷达图说明',
    code_technology_pie         TEXT                                                               NULL COMMENT '代码技术-饼状图',
    code_catalog_path           TEXT                                                               NULL COMMENT '代码目录-树状图',
    create_time                 DATETIME     DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    update_time                 DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    is_delete                   TINYINT(1)   DEFAULT FALSE                                         NOT NULL COMMENT '是否逻辑删除',
    CONSTRAINT fk_data_charts_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_data_charts_id_is_delete (id, is_delete),
    INDEX idx_data_charts_user_id (user_id)
) COMMENT '数据信息表' COLLATE = utf8mb4_unicode_ci;

-- 数据表 data_charts
CREATE TABLE IF NOT EXISTS data_charts_0
(
    id                          BIGINT AUTO_INCREMENT COMMENT '结果ID' PRIMARY KEY,
    goal_description            VARCHAR(255)                                                       NULL COMMENT '目标描述',
    generation_name             VARCHAR(100)                                                       NULL COMMENT '生成名称',
    ai_token_usage              INT UNSIGNED DEFAULT 0                                             NULL COMMENT 'AI使用量',
    user_id                     BIGINT                                                             NOT NULL COMMENT '用户ID',
    raw_data                    TEXT                                                               NULL COMMENT '原始数据',
    code_comments               TEXT                                                               NULL COMMENT '代码注释',
    code_profile_description    TEXT                                                               NULL COMMENT '代码简介',
    code_entities               TEXT                                                               NULL COMMENT '代码实体',
    code_apis                   TEXT                                                               NULL COMMENT '代码API',
    code_execution              TEXT                                                               NULL COMMENT '代码执行',
    code_suggestions            TEXT                                                               NULL COMMENT '代码建议',
    code_norm_radar             TEXT                                                               NULL COMMENT '代码规范-雷达图',
    code_norm_radar_description TEXT                                                               NULL COMMENT '代码规范-雷达图说明',
    code_technology_pie         TEXT                                                               NULL COMMENT '代码技术-饼状图',
    code_catalog_path           TEXT                                                               NULL COMMENT '代码目录-树状图',
    create_time                 DATETIME     DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    update_time                 DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    is_delete                   TINYINT(1)   DEFAULT FALSE                                         NOT NULL COMMENT '是否逻辑删除',
    CONSTRAINT fk_data_charts_0_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_data_charts_0_id_is_delete (id, is_delete),
    INDEX idx_data_charts_0_user_id (user_id)
) COMMENT '数据信息表' COLLATE = utf8mb4_unicode_ci;
-- 数据表 data_charts
CREATE TABLE IF NOT EXISTS data_charts_1
(
    id                          BIGINT AUTO_INCREMENT COMMENT '结果ID' PRIMARY KEY,
    goal_description            VARCHAR(255)                                                       NULL COMMENT '目标描述',
    generation_name             VARCHAR(100)                                                       NULL COMMENT '生成名称',
    ai_token_usage              INT UNSIGNED DEFAULT 0                                             NULL COMMENT 'AI使用量',
    user_id                     BIGINT                                                             NOT NULL COMMENT '用户ID',
    raw_data                    TEXT                                                               NULL COMMENT '原始数据',
    code_comments               TEXT                                                               NULL COMMENT '代码注释',
    code_profile_description    TEXT                                                               NULL COMMENT '代码简介',
    code_entities               TEXT                                                               NULL COMMENT '代码实体',
    code_apis                   TEXT                                                               NULL COMMENT '代码API',
    code_execution              TEXT                                                               NULL COMMENT '代码执行',
    code_suggestions            TEXT                                                               NULL COMMENT '代码建议',
    code_norm_radar             TEXT                                                               NULL COMMENT '代码规范-雷达图',
    code_norm_radar_description TEXT                                                               NULL COMMENT '代码规范-雷达图说明',
    code_technology_pie         TEXT                                                               NULL COMMENT '代码技术-饼状图',
    code_catalog_path           TEXT                                                               NULL COMMENT '代码目录-树状图',
    create_time                 DATETIME     DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    update_time                 DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    is_delete                   TINYINT(1)   DEFAULT FALSE                                         NOT NULL COMMENT '是否逻辑删除',
    CONSTRAINT fk_data_charts_1_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_data_charts_1_id_is_delete (id, is_delete),
    INDEX idx_data_charts_1_user_id (user_id)
) COMMENT '数据信息表' COLLATE = utf8mb4_unicode_ci;
-- 帖子表 posts
CREATE TABLE IF NOT EXISTS posts
(
    id              BIGINT AUTO_INCREMENT COMMENT '帖子ID' PRIMARY KEY,
    likes_count     INT        DEFAULT 0                                             NOT NULL COMMENT '点赞数',
    favorites_count INT        DEFAULT 0                                             NOT NULL COMMENT '收藏数',
    content         VARCHAR(1020)                                                    NULL COMMENT '分享描述内容',
    user_id         BIGINT                                                           NOT NULL COMMENT '创建用户ID',
    post_id         BIGINT                                                           NOT NULL COMMENT '结果ID ',
    create_time     DATETIME   DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    update_time     DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    is_delete       TINYINT(1) DEFAULT FALSE                                         NOT NULL COMMENT '是否删除',
    CONSTRAINT fk_posts_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_posts_post_id FOREIGN KEY (post_id) REFERENCES data_charts (id) ON DELETE CASCADE,
    CONSTRAINT check_positive_likes_count CHECK (likes_count >= 0),
    CONSTRAINT check_positive_favorites_count CHECK (favorites_count >= 0),
    INDEX idx_posts_id_is_delete (id, is_delete),
    INDEX idx_posts_user_id (user_id),
    INDEX idx_posts_post_id (post_id)
) COMMENT '帖子信息表' COLLATE = utf8mb4_unicode_ci;

-- 帖子点赞表 post_likes
CREATE TABLE IF NOT EXISTS post_likes
(
    id          BIGINT AUTO_INCREMENT COMMENT '点赞ID' PRIMARY KEY,
    post_id     BIGINT                                                         NOT NULL COMMENT '结果ID',
    user_id     BIGINT                                                         NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    INDEX idx_post_likes_post_id (post_id),
    INDEX idx_post_likes_user_id (user_id)
) COMMENT '帖子点赞信息表' COLLATE = utf8mb4_unicode_ci;

-- 帖子收藏表 post_favorites
CREATE TABLE IF NOT EXISTS post_favorites
(
    id          BIGINT AUTO_INCREMENT COMMENT '收藏ID' PRIMARY KEY,
    post_id     BIGINT                                                         NOT NULL COMMENT '结果ID',
    user_id     BIGINT                                                         NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    INDEX idx_post_favorites_post_id (post_id),
    INDEX idx_post_favorites_user_id (user_id)
) COMMENT '帖子收藏信息表' COLLATE = utf8mb4_unicode_ci;

-- 帖子评论表 post_comments
CREATE TABLE IF NOT EXISTS post_comments
(
    id           BIGINT AUTO_INCREMENT COMMENT '评论ID' PRIMARY KEY,
    post_id      BIGINT                                                           NOT NULL COMMENT '结果ID',
    user_id      BIGINT                                                           NOT NULL COMMENT '创建用户ID',
    comment_text VARCHAR(1020)                                                    NOT NULL COMMENT '评论内容',
    create_time  DATETIME   DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    update_time  DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    is_delete    TINYINT(1) DEFAULT FALSE                                         NOT NULL COMMENT '是否删除',
    CONSTRAINT fk_post_comments_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_comments_post_id FOREIGN KEY (post_id) REFERENCES data_charts (id) ON DELETE CASCADE,
    INDEX idx_post_comments_id_is_delete (id, is_delete),
    INDEX idx_post_comments_post_id (post_id),
    INDEX idx_post_comments_user_id (user_id)
) COMMENT '帖子评论信息表' COLLATE = utf8mb4_unicode_ci;

