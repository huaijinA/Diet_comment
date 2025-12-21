package com.example.diet_comment.service;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.diet_comment.model.Result;
import com.example.diet_comment.model.User;
import com.example.diet_comment.model.DTO.UserDTO;

public interface UserService extends IService<User> {
    //定义 register, login 等方法
	public boolean register(User user);
	public String login(User user);
	UserDTO getUserDTOById(Integer id);
	Result pwdchange(Integer userId, String oldpwd, String newpwd);
    User getByUserName(String userName);
}