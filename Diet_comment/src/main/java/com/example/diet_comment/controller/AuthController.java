package com.example.diet_comment.controller;

import com.example.diet_comment.model.Result;
import com.example.diet_comment.model.User;
import com.example.diet_comment.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
		boolean success = userService.register(user);
       if(success) {
           return Result.success("Registration successful");
       }
       return Result.error("Registration failed");
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        String jwt = userService.login(user);
        if (jwt != null) {
            return Result.success(jwt);
        }
        return Result.error("Login failed");
    }
}

        
		


