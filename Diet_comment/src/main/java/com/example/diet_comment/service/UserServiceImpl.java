package com.example.diet_comment.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.diet_comment.mapper.UserMapper;
import com.example.diet_comment.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    //register, login 等方法的具体逻辑
}