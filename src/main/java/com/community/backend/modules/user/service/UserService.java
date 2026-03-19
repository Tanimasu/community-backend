package com.community.backend.modules.user.service;

import com.community.backend.modules.user.entity.User;
import com.community.backend.modules.user.mapper.UserMapper;
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
}