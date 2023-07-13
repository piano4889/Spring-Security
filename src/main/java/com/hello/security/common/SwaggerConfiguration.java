package com.hello.security.common;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String springdocVersion) {
        Info info = new Info()
                .title("게시판 구현")
                .version(springdocVersion)
                .description("게시판/댓글 CRUD, 파일 업/다운로드 구현");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
