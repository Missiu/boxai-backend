package com.boxai.config.cors;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

/**
 * 全局跨域配置
 */
@Configuration
@Slf4j
public class CorsConfig implements WebMvcConfigurer  {

    /**
     * 添加跨域配置
     *
     * @param registry 跨域注册器
     */

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有路径进行 CORS 配置
                .allowedOrigins("http://localhost:8000") // 允许特定的域名和端口
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                .allowCredentials(true) // 允许发送 Cookie
                .maxAge(3600); // 设置缓存时间为1小时
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}