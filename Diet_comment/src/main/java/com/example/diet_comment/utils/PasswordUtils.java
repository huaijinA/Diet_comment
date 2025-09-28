package com.example.diet_comment.utils;


import org.mindrot.jbcrypt.BCrypt;

public class    PasswordUtils {

    // 加密密码
    public static String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    // 验证密码
    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}