package com.hedgehog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hedgehog.common.Result;
import com.hedgehog.entity.User;
import com.hedgehog.entity.UserPageRequest;
import com.hedgehog.entity.UserSaveRequest;
import com.hedgehog.service.UserService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<IPage<User>> page(UserPageRequest request) {
        return Result.ok(userService.page(request));
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody UserSaveRequest request) {
        userService.save(request);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserSaveRequest request) {
        request.setId(id);
        userService.update(request);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }
}
