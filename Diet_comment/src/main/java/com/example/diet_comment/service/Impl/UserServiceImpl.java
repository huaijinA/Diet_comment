package com.example.diet_comment.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.diet_comment.mapper.UserMapper;
import com.example.diet_comment.model.Result;
import com.example.diet_comment.model.User;
import com.example.diet_comment.model.DTO.UserDTO;
import com.example.diet_comment.service.UserService;
import com.example.diet_comment.utils.Jwt;
import com.example.diet_comment.utils.PasswordUtils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    //register, login 等方法的具体逻辑

	@Override
	public boolean register(User user) {
		String email = user.getEmail();
		String userName = user.getUserName();
		String password = user.getPassword();

		if (email == null || email.isEmpty() || userName == null || userName.isEmpty()) {
			return false; // 无效输入
		}
		if (this.count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("email", email)) > 0) {
			return false; // 邮箱已存在
		}
		if (this.count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("username", userName)) > 0) {
			return false; // 用户名已存在
		}
		if (password == null || password.isEmpty()) {
			return false; // 密码不能为空
		}

		String passwordHash = PasswordUtils.hashPassword(password);
		user.setPassword(passwordHash);

		this.save(user);

		return true;

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
	
}