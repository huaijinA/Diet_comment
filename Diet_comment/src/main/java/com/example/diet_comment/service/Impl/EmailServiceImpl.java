package com.example.diet_comment.service.Impl;

import com.example.diet_comment.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.mail.from}")
    private String fromEmail;

    private static final String VERIFICATION_CODE_KEY_PREFIX = "verification_code:";

    @Override
    public String sendVerificationCode(String toEmail) {
        try {
            // 生成6位随机验证码
            String code = String.format("%06d", new Random().nextInt(999999));

            // 构造邮件内容
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("您的注册验证码");
            message.setText("您的验证码是：" + code + "，有效期为5分钟。");

            // 发送邮件
            mailSender.send(message);

            // 将验证码存入Redis，有效期5分钟
            redisTemplate.opsForValue().set(VERIFICATION_CODE_KEY_PREFIX + toEmail, code, 5, TimeUnit.MINUTES);

            return "success";
        } catch (Exception e) {
            // 记录日志
            e.printStackTrace();
            return "邮件发送失败，请稍后重试。";
        }
    }
}
