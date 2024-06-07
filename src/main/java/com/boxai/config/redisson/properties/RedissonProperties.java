package com.boxai.config.redisson.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.config.ClusterServersConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Redisson配置属性
 *
 */
@Data
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {

    /**
     * 线程池数量,默认值 = 当前处理核数量 * 2
     */
    private Integer threads;

    /**
     * Netty线程池数量,默认值 = 当前处理核数量 * 2
     */
    private Integer nettyThreads;

    /**
     * 限流单位时间，单位：秒
     */
    private Long limitRateInterval;

    /**
     * 限流单位时间内访问次数，也能看做单位时间内系统分发的令牌数
     */
    private Long limitRate;

    /**
     * 每个操作所要消耗的令牌数
     */
    private Long limitPermits;

    /**
     * 单机服务配置
     */
    private SingleServerConfig singleServerConfig;

    /**
     * 集群服务配置
     */
    private ClusterServersConfig clusterServersConfig;

    /**
     * 单机服务配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleServerConfig {

        /**
         * 是否启动单机Redis（Redisson）缓存
         */
        private Boolean enableSingle = true;

        /**
         * 单机地址（一定要在redis协议下）
         */
        private String address;

        /**
         * # 数据库索引
         */
        private Integer database;

        /**
         * 密码（考虑是否需要密码）
         */
        private String password;

        /**
         * 命令等待超时，单位：毫秒
         */
        private Integer timeout;

        /**
         * 发布和订阅连接池大小
         */
        private Integer subscriptionConnectionPoolSize;

        /**
         * 最小空闲连接数
         */
        private Integer connectionMinimumIdleSize;

        /**
         * 连接池大小
         */
        private Integer connectionPoolSize;

        /**
         * 连接空闲超时，单位：毫秒
         */
        private Integer idleConnectionTimeout;

    }
}