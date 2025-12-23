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

import java.util.Objects;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
		String mes = userService.register(user);
       if(Objects.equals(mes, "success")) {
           return Result.success("Registration successful");
       }
       return Result.error(mes);
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

        
		


