package com.example.diet_comment.service;

public interface EmailService {
    /**
     * 发送验证码到指定邮箱
     * @param toEmail 目标邮箱地址
     * @return 返回发送结果，成功则为 "success"，否则为错误信息
     */
    String sendVerificationCode(String toEmail);
}