package com.smileqi.common.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI smileQiOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("smileqi-backend")
                        .description("SMILEQI文档")
                        .version("v1")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("外部文档")
                        .url("https://springshop.wiki.github.org/docs"));
    }


    // 接口较多，可以进行分组，此处进行注释
/*
    @Bean
    public GroupedOpenApi backendGroup() {
        return GroupedOpenApi.builder().group("user").displayName("user")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title("UJCMS 后台 API").version("1.0.0")))
                .packagesToScan("com.smileqi.web.controller.user")
                .pathsToMatch("/**")
                .build();
    }
*/
}