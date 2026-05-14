package com.hedgehog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hedgehog.mapper")
public class HedgehogApplication {
    public static void main(String[] args) {
        SpringApplication.run(HedgehogApplication.class, args);
    }
}
