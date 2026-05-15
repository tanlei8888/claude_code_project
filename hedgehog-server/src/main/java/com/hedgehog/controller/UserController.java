package com.hedgehog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hedgehog.common.Result;
import com.hedgehog.entity.User;
import com.hedgehog.entity.UserPageRequest;
import com.hedgehog.entity.UserSaveRequest;
import com.hedgehog.service.UserService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器，处理用户的增删改查。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** GET /api/users — 分页查询用户 */
    @GetMapping
    public Result<IPage<User>> page(UserPageRequest request) {
        return Result.ok(userService.page(request));
    }

    /** GET /api/users/{id} — 获取用户详情 */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    /** POST /api/users — 新增用户 */
    @PostMapping
    public Result<Void> save(@Valid @RequestBody UserSaveRequest request) {
        userService.save(request);
        return Result.ok();
    }

    /** PUT /api/users/{id} — 更新用户 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserSaveRequest request) {
        request.setId(id);
        userService.update(request);
        return Result.ok();
    }

    /** DELETE /api/users/{id} — 删除用户 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }
}
