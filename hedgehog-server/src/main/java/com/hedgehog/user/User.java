package com.hedgehog.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，对应 sys_user 表。
 */
@Data
@TableName("sys_user")
public class User {
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 用户名 */
    private String username;
    /** 密码（BCrypt 加密存储） */
    private String password;
    /** 昵称 */
    private String nickname;
    /** 邮箱 */
    private String email;
    /** 手机号 */
    private String phone;
    /** 状态：0=禁用 1=启用 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 逻辑删除：0=未删除 1=已删除 */
    @TableLogic
    private Integer deleted;
    /** 角色：ADMIN=管理员 USER=普通用户 */
    private String role;
    /** 头像 URL */
    private String avatar;
    /** 个人简介 */
    private String bio;
}
