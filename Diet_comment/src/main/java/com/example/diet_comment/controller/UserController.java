package com.example.diet_comment.controller;

import com.example.diet_comment.model.DTO.PwdChangeReq;
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
        System.out.println("userid"+currentUserId);
		UserDTO userDTO = userService.getUserDTOById(currentUserId);
		
		if(userDTO!=null) {
			return Result.success((userDTO));
		}

		return Result.error("User not found");
	}



    @PutMapping(value = "/userpage", consumes = "multipart/form-data")
    public Result updateUserInfo(HttpServletRequest request,
                                 @RequestParam(required = false) String userName,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) MultipartFile image) {
        Integer currentUserId = (Integer) request.getAttribute("userId");
        User user = userService.getById(currentUserId);
        System.out.println("Updating user info for userId: " + currentUserId);

        // 先校验用户名是否被其他用户占用（需要在 UserService 中提供 getByUserName 方法）
        if (userName != null && !userName.isEmpty()) {
            User exist = userService.getByUserName(userName);
            if (exist != null && !exist.getId().equals(currentUserId)) {
                return Result.error("用户名已存在");
            }
            user.setUserName(userName);
        }

        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }

        if (image != null && !image.isEmpty()) {

                try {
                    // 删除旧图片记录
                    // imageService.deleteByTypeAndId("user", currentUserId);
                    System.out.println("删除旧user图片记录"+currentUserId);

                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.error("删除图片时发生错误: " + e.getMessage());
                }

            String avatarUrl = imageService.uploadImageById(currentUserId, image, "user");
            user.setAvatarUrl(avatarUrl);
        }

        try {
            if (userService.updateById(user)) {
                return Result.success();
            } else {
                return Result.error("Update failed");
            }
        } catch (org.springframework.dao.DuplicateKeyException ex) {
            // 安全兜底：如果数据库唯一索引仍然抛出异常，返回友好提示
            return Result.error(ex.getMessage());
        } catch (Exception ex) {
            return Result.error("Update failed"+ex.getMessage());
        }
    }

	@GetMapping("/user/{id}")
	public Result getUserById(@PathVariable Integer id) {
		UserDTO userdto=userService.getUserDTOById(id);
		if(userdto!=null) {
			return Result.success(userdto);
		}
		return Result.error("User not found");
	}


    @PostMapping(value = "/userpage/pwdput", consumes = "application/json")
    public Result pwdput(@RequestBody PwdChangeReq req, HttpServletRequest request) {
        Integer currentUserId = (Integer) request.getAttribute("userId");
        return userService.pwdchange(currentUserId, req.getOldpwd(), req.getNewpwd());
    }
	



}