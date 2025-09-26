package com.example.diet_comment.controller;

import com.example.diet_comment.model.User;
import com.example.diet_comment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/userpage")
    public User getUserInfo() {
        // TODO: 应从安全上下文中获取当前用户ID，这里暂时写死为1
        Long currentUserId = 1L;
        return userService.getById(currentUserId);
    }

    @PutMapping("/userpage")
    public User updateUserInfo(@RequestBody User user) {
        // TODO: 应从安全上下文中获取当前用户ID，并设置到user对象中，防止越权修改
        // user.setId(currentUserId);
        userService.updateById(user);
        return user;
    }
}