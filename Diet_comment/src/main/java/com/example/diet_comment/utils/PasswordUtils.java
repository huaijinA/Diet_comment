package com.example.diet_comment.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 加密密码
    public static String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // 验证密码
    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}