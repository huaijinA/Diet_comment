package com.example.diet_comment.controller;

import com.example.diet_comment.model.User;
import com.example.diet_comment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // TODO: 添加密码加密、用户名/邮箱校验等逻辑
        boolean success = userService.save(user);
        return success ? "Registration successful" : "Registration failed";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        // TODO: 实现登录验证逻辑，例如使用 Spring Security 和 JWT
        return "Login endpoint hit. User: " + user.getUsername();
    }
}