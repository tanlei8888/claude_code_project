CREATE DATABASE IF NOT EXISTS hedgehog DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;

USE hedgehog;

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(50)  NOT NULL            COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL            COMMENT '密码（BCrypt 加密）',
    `nickname`    VARCHAR(50)  DEFAULT NULL        COMMENT '昵称',
    `email`       VARCHAR(100) DEFAULT NULL        COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL        COMMENT '手机号',
    `status`      TINYINT      NOT NULL DEFAULT 1  COMMENT '状态 1-启用 0-禁用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0  COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 初始管理员账号 admin / admin123 (BCrypt)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '超级管理员', 1);
