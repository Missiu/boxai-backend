package com.boxai.common.constants;
/**
 * Redis键名前缀、过期时间等常量接口
 */
public interface RedisKeyConstant {
    /**
     * 用户登录token
     */
    String USER_LOGIN_TOKEN = "user:login:token:";
    /**
     * 用户登录token过期时间
     */
    Long USER_LOGIN_TOKEN_TTL = 30L;
    /**
     * AI生成内容
     */
    String AI_GENERATION_CONTENT = "ai:generation:content:";
    /**
     * AI生成内容过期时间
     */
    Long AI_GENERATION_CONTENT_TTL = 30L;
    /**
     * 帖子分享内容
     */
    String POST_SHARE_COUNT = "post:share:content:";
    /**
     * 帖子分享内容过期时间
     */
    Long POST_SHARE_COUNT_TTL = 10L;
    /**
     * 帖子收藏操作
     */
    String POST_FAVORITES_COUNT = "post:favorites:content:";
    /**
     * 帖子收藏操作过期时间
     */
    Long POST_FAVORITES_COUNT_TTL = 5L;
    /**
     * 帖子点赞操作
     */
    String POST_LIKES_COUNT = "post:likes:content:";
    /**
     * 帖子点赞次数过期时间
     */
    Long POST_LIKES_COUNT_TTL = 5L;

    /**
     * 限流
     */
    String RATE_LIMIT_AI_GENERATION = "rate_limit_ai";
}
