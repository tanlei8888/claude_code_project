package com.hedgehog.config;

import com.hedgehog.entity.User;
import com.hedgehog.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动数据初始化器。
 *
 * <p>启动时检查用户表是否为空，如果为空则自动创建默认管理员账号：
 * 用户名 admin / 密码 admin123 / 角色 ADMIN。
 * 密码使用 BCrypt 加密存储。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        long count = userService.count();
        if (count == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("超级管理员");
            admin.setStatus(1);
            admin.setRole("ADMIN");
            userService.save(admin);
        }
    }
}
