package com.hedgehog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全相关配置。
 *
 * <p>将 BCryptPasswordEncoder 注册为 Spring Bean，统一注入使用。
 */
@Configuration
public class SecurityConfig {

    /** BCrypt 密码编码器，用于密码加密和校验 */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
