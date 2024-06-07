package com.boxai.config.redisson;

import com.boxai.config.redisson.properties.RedissonProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
@Slf4j
public class RedissonConfig {

    @Resource
    private RedissonProperties redissonProperties;
    @Resource
    private ObjectMapper objectMapper;
    /**
     * 创建单机版的Redisson客户端。
     *
     * @return RedissonClient 单机版Redisson客户端实例
     */
    @Bean
    public RedissonClient singleClient() {
        Config config = new Config();
        // 设置线程和Netty线程数
        config.setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                // 设置JSON编解码器
                .setCodec(new JsonJacksonCodec(objectMapper));

        RedissonProperties.SingleServerConfig singleServerConfig = redissonProperties.getSingleServerConfig();
        // 配置单机模式连接参数
        // 配置连接到单个服务器
        config.useSingleServer()
                // 设置服务器地址
                .setAddress(singleServerConfig.getAddress())
                // 设置数据库索引
                .setDatabase(singleServerConfig.getDatabase())
                // 设置密码
                .setPassword(singleServerConfig.getPassword())
                // 设置操作超时时间
                .setTimeout(singleServerConfig.getTimeout())
                // 设置空闲连接超时时间
                .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                // 设置订阅连接池大小
                .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                // 设置连接最小空闲大小
                .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                // 设置连接池大小
                .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());


        return Redisson.create(config);
    }
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }
}
