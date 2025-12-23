package com.example.diet_comment.controller;

import com.example.diet_comment.model.DTO.UserReg;
import com.example.diet_comment.model.Result;
import com.example.diet_comment.model.User;
import com.example.diet_comment.service.EmailService;
import com.example.diet_comment.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public Result register(@RequestBody UserReg user) {
		String mes = userService.register(user);
       if(Objects.equals(mes, "success")) {
           return Result.success("Registration successful");
       }
       return Result.error(mes);
    }


    @PostMapping("/send-code")
    public Result sendCode(@RequestParam String email) {
        String result = emailService.sendVerificationCode(email);
        if ("success".equals(result)) {
            return Result.success("验证码已发送");
        }
        return Result.error(result);
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

        
		


