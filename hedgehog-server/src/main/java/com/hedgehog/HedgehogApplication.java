package com.hedgehog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hedgehog 博客系统后端启动类。
 *
 * <p>启用自动配置并扫描 com.hedgehog.mapper 包下的 MyBatis Plus Mapper 接口。
 */
@SpringBootApplication
@MapperScan("com.hedgehog.mapper")
public class HedgehogApplication {
    public static void main(String[] args) {
        SpringApplication.run(HedgehogApplication.class, args);
    }
}
