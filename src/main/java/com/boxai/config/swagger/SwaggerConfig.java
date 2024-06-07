package com.boxai.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@Slf4j
public class SwaggerConfig {
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                // 配置全局鉴权参数
                .components(new Components()
                        // 添加一个名为AUTHORIZATION的安全方案，用于API的授权
                        .addSecuritySchemes(HttpHeaders.AUTHORIZATION,
                                new SecurityScheme()
                                        .name(HttpHeaders.AUTHORIZATION) // 安全方案的名称
                                        .type(SecurityScheme.Type.APIKEY) // 安全方案的类型
                                        .in(SecurityScheme.In.HEADER) // 安全方案的位置，头部
                                        .scheme("Bearer") // 使用Bearer作为方案
                                        .bearerFormat("JWT") // 令牌格式为JWT
                        )
                );
    }

    /**
     * 全局OpenAPI自定义定制器。
     * 比如，可以在这个定制器中添加全局的参数、响应等。
     *
     * @return 返回配置好的GlobalOpenApiCustomizer实例。
     */
    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            // 给所有路径添加鉴权，除了登录和注册接口
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((s, pathItem) -> {
                    // 排除不需要鉴权的接口
                    if (s.contains("register")){
                        return;
                    }
                    // 为其他接口添加鉴权
                    pathItem.readOperations()
                            .forEach(operation ->
                                    operation.addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                            );
                });
            }
        };
    }
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }
}
