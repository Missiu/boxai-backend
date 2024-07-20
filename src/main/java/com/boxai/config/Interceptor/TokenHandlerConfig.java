package com.boxai.config.Interceptor;

import com.boxai.config.Interceptor.token.TokenInterceptorHandler;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
@Slf4j
public class TokenHandlerConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        // 覆盖所有请求
//        registry.addMapping("/**")
//                // 允许发送 Cookie
//                .allowCredentials(true)
//                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .exposedHeaders("*");
//    }

    /**
     * 向拦截器注册表中添加拦截器
     *
     * @param registry 拦截器注册表，用于注册和管理拦截器。
     */

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        try {
            log.info("Registering TokenInterceptorHandler");
            // 创建TokenInterceptorHandler实例
            TokenInterceptorHandler interceptorHandler = new TokenInterceptorHandler(stringRedisTemplate);

            // 配置拦截器路径和排除路径
            registry.addInterceptor(interceptorHandler)
                    .addPathPatterns("/**")
                    .excludePathPatterns("/swagger-resources/**","/swagger-ui/**", "/v3/**", "/error","/","/doc.html/**","/doc/**","/webjars/**","/v2/**","/swagger-ui.html")
                    .excludePathPatterns("/user/login", "/user/register");

            log.info("TokenInterceptorHandler registered successfully.");
        } catch (Exception e) {
            log.error("Error while registering TokenInterceptorHandler: {}", e.getMessage());
        }
    }



    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }
}
