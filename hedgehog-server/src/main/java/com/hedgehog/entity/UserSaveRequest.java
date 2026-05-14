package com.hedgehog.entity;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSaveRequest {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;
    private String nickname;
    private String email;
    private String phone;
    private Integer status;
}
