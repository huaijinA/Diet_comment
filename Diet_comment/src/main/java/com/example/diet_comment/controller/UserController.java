package com.example.diet_comment.controller;

import com.example.diet_comment.model.Image;
import com.example.diet_comment.model.Result;
import com.example.diet_comment.model.User;
import com.example.diet_comment.model.DTO.UserDTO;
import com.example.diet_comment.service.ImageService;
import com.example.diet_comment.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class UserController {

    @Autowired
    private UserService userService;

	@Autowired
	private ImageService imageService;

    @GetMapping("/userpage")
    public Result getUserInfo(HttpServletRequest request) {

        Integer currentUserId = (Integer) request.getAttribute("userId");
		UserDTO userDTO = userService.getUserDTOById(currentUserId);
		
		if(userDTO!=null) {
			return Result.success((userDTO));
		}

		return Result.error("User not found");
	}



    @PutMapping("/userpage")
    public Result updateUserInfo( HttpServletRequest request,@RequestParam(required = false) String username,
	@RequestParam(required = false) MultipartFile image) {

        Integer currentUserId = (Integer) request.getAttribute("userId");
        User user = userService.getById(currentUserId);

		if(username!=null && !username.isEmpty()) {
			user.setUserName(username);
		}

		if(image!=null && !image.isEmpty()) {
			String avatarUrl = imageService.uploadImage(currentUserId,image);
			user.setAvatarUrl(avatarUrl);
		}

		if(userService.updateById(user)) {
            return Result.success();
        }
        return Result.error("Update failed");
	}

	@GetMapping("/user/{id}")
	public Result getUserById(@PathVariable Integer id) {
		UserDTO userdto=userService.getUserDTOById(id);
		if(userdto!=null) {
			return Result.success(userdto);
		}
		return Result.error("User not found");
	}


	@PostMapping("/userpage/pwdput")
	public Result pwdput(String oldpwd, String newpwd, HttpServletRequest request) {
		Integer currentUserId = (Integer) request.getAttribute("userId");
		return userService.pwdchange(currentUserId, oldpwd, newpwd);
	}
	



}