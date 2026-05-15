package com.hedgehog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedgehog.common.ResultCode;
import com.hedgehog.entity.User;
import com.hedgehog.entity.UserPageRequest;
import com.hedgehog.entity.UserSaveRequest;
import com.hedgehog.exception.BusinessException;
import com.hedgehog.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现。
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public IPage<User> page(UserPageRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 用户名和昵称联合模糊搜索
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(User::getUsername, request.getKeyword())
                    .or()
                    .like(User::getNickname, request.getKeyword());
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> page = new Page<>(request.getPage(), request.getSize());
        IPage<User> result = baseMapper.selectPage(page, wrapper);
        // 脱敏：清除密码字段
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }

    @Override
    public User getById(Long id) {
        User user = super.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 脱敏：清除密码字段
        user.setPassword(null);
        return user;
    }

    @Override
    public void save(UserSaveRequest request) {
        User exist = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (exist != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        User user = new User();
        user.setUsername(request.getUsername());
        // 密码为空时默认 123456，使用 BCrypt 加密
        user.setPassword(passwordEncoder.encode(
                StringUtils.hasText(request.getPassword()) ? request.getPassword() : "123456"));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        super.save(user);
    }

    @Override
    public void update(UserSaveRequest request) {
        User user = super.getById(request.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 检查用户名唯一性（排除自身）
        User exist = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .ne(User::getId, request.getId()));
        if (exist != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        user.setUsername(request.getUsername());
        // 仅在新密码不为空时才更新密码
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus());
        super.updateById(user);
    }

    @Override
    public void delete(Long id) {
        User user = super.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        super.removeById(id);
    }
}
