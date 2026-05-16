package com.hedgehog.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口。
 */
public interface UserService extends IService<User> {
    /**
     * 分页查询用户，支持按用户名或昵称模糊搜索。
     */
    IPage<User> page(UserPageRequest request);

    /**
     * 通过 ID 获取用户详情。
     */
    User getById(Long id);

    /**
     * 创建用户，密码为空时默认 123456，密码通过 BCrypt 加密存储。
     */
    void save(UserSaveRequest request);

    /**
     * 更新用户信息。
     */
    void update(UserSaveRequest request);

    /**
     * 逻辑删除用户。
     */
    void delete(Long id);
}
