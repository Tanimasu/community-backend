package com.community.backend.modules.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.backend.modules.user.entity.User;
import com.community.backend.modules.user.mapper.UserMapper;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void createUser(User user) {
        userMapper.insert(user);
    }

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public User getByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    // 批量查询用户，返回 id -> User，用于给列表数据填充作者信息
    public Map<Long, User> getUserMap(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return userMapper.selectBatchIds(ids.stream().distinct().toList()).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
