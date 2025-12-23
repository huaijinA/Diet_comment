package com.example.diet_comment.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.diet_comment.mapper.UserMapper;
import com.example.diet_comment.model.DTO.UserReg;
import com.example.diet_comment.model.Result;
import com.example.diet_comment.model.User;
import com.example.diet_comment.model.DTO.UserDTO;
import com.example.diet_comment.service.EmailService;
import com.example.diet_comment.service.UserService;
import com.example.diet_comment.utils.Jwt;
import com.example.diet_comment.utils.PasswordUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    //register, login 等方法的具体逻辑


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String VERIFICATION_CODE_KEY_PREFIX = "verification_code:";
    @Autowired
    private EmailService emailService;

    @Override
	public String register(UserReg user) {
		String email = user.getEmail();
		String userName = user.getUserName();
		String password = user.getPassword();
        String code = user.getCode();

		if (email == null || email.isEmpty() || userName == null || userName.isEmpty()) {
			return "无效输入"; // 无效输入
		}
		if (this.count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("email", email)) > 0) {
			return "邮箱已存在"; // 邮箱已存在
		}
		if (this.count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("username", userName)) > 0) {
			return "用户名已存在"; // 用户名已存在
		}
		if (password == null || password.isEmpty()) {
			return "密码不能为空"; // 密码不能为空
		}



        String redisCode = (String) redisTemplate.opsForValue().get(VERIFICATION_CODE_KEY_PREFIX + email);
        if (redisCode == null) {
            return "验证码已过期，请重新获取";
        }
        if (!Objects.equals(redisCode, code)) {
            return "验证码错误";
        }



		String passwordHash = PasswordUtils.hashPassword(password);
		user.setPassword(passwordHash);
        User newUser = user.toUserEntity();

		this.save(newUser);
        redisTemplate.delete(VERIFICATION_CODE_KEY_PREFIX + email);

		return "success";

	}


	@Override
	public String login(User user) {
		String userName = user.getUserName();
		String password = user.getPassword();
		if (userName == null || password == null) {
			return null;
		}
		User dbUser = this.getOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("username", userName));
		if (dbUser == null) {
			return null;
		}
		if (PasswordUtils.verifyPassword(password, dbUser.getPassword())) {
			String jwt = Jwt.generateJwt(dbUser.getId().toString(), dbUser.getUserName());
			return jwt;
		}

		return null;
	}

	@Override
	public UserDTO getUserDTOById(Integer id) {
		User user = this.getById(id);
		if (user == null) {
			return null;
		}
		UserDTO userDTO = new UserDTO();
		return userDTO.fromUser(user);
	}



	@Override
	public Result pwdchange(Integer userId, String oldpwd, String newpwd) {
        System.out.println(oldpwd+newpwd);
		User user = this.getById(userId);
		if (user == null) {
			return Result.error("User not found");
		}
		if (!PasswordUtils.verifyPassword(oldpwd, user.getPassword())) {
			return Result.error("Old password is incorrect");
		}
		if (newpwd == null || newpwd.isEmpty()) {
			return Result.error("New password cannot be empty");
		}
		String newHashedPwd = PasswordUtils.hashPassword(newpwd);
		user.setPassword(newHashedPwd);
		if (this.updateById(user)) {
			return Result.success();
		} else {
			return Result.error("Failed to change password");
		}
	}

    @Override
    public User getByUserName(String userName) {
        return this.getOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("username", userName));
    }


}