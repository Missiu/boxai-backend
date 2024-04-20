package com.boxai.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类，用于配置MyBatis Plus的相关设置。
 */
@Configuration
@MapperScan("com.boxai.mapper") // 指定Mapper接口所在的包
public class MyBatisPlusConfig {

    /**
     * 配置Mybatis Plus拦截器。
     * <p>该方法返回一个MybatisPlusInterceptor实例，用于拦截MyBatis Plus的执行流程，添加插件等设置。</p>
     * @return MybatisPlusInterceptor实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件，支持MySQL分页
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
