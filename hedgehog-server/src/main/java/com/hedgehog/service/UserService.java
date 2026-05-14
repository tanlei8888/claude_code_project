package com.hedgehog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hedgehog.entity.User;
import com.hedgehog.entity.UserPageRequest;
import com.hedgehog.entity.UserSaveRequest;

public interface UserService extends IService<User> {
    IPage<User> page(UserPageRequest request);
    User getById(Long id);
    void save(UserSaveRequest request);
    void update(UserSaveRequest request);
    void delete(Long id);
}
