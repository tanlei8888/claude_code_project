package com.hedgehog.entity;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户保存请求 DTO，创建和编辑共用。
 */
@Data
public class UserSaveRequest {
    /** 用户 ID，编辑时必传，创建时为空 */
    private Long id;

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码，创建时未提供则默认 123456 */
    private String password;
    /** 昵称 */
    private String nickname;
    /** 邮箱 */
    private String email;
    /** 手机号 */
    private String phone;
    /** 头像 URL */
    private String avatar;
    /** 个人简介 */
    private String bio;
    /** 角色：ADMIN / USER */
    private String role;
    /** 状态：0=禁用 1=启用 */
    private Integer status;
}
