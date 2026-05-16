package com.hedgehog;

import com.hedgehog.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Hedgehog 博客系统后端启动类。
 *
 * <p>Mapper 接口均标注 {@code @Mapper}，由 MyBatis 自动注册，无需 @MapperScan。
 */
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class HedgehogApplication {
    public static void main(String[] args) {
        SpringApplication.run(HedgehogApplication.class, args);
    }
}
