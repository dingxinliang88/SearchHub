package com.juzi.quickweb.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Knife4j 接口文档配置
 * <a href="https://doc.xiaominfo.com/knife4j/documentation/get_start.html">Knife4j</a>
 *
 * @author codejuzi
 */
@Data
@Configuration
@EnableSwagger2
@Profile({"dev", "test"})
@ConfigurationProperties(prefix = "knife4j.config")
public class Knife4jConfig {

    private static final String DEFAULT_VAL = "";

    private String title = "API接口文档";

    private String description = DEFAULT_VAL;

    private String version = "0.0.1";

    private String contactName = DEFAULT_VAL;
    private String contactUrl = DEFAULT_VAL;
    private String contactEmail = DEFAULT_VAL;

    private String license = DEFAULT_VAL;
    private String licenseUrl = DEFAULT_VAL;

    private String basePackage;

    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(new Contact(contactName, contactUrl, contactEmail))
                        .license(license)
                        .licenseUrl(licenseUrl)
                        .build())
                .select()
                // 指定 Controller 扫描包路径
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build();
    }
}
