package com.example.diet_comment.service.Impl;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.diet_comment.mapper.ImageMapper;
import com.example.diet_comment.model.Image;
import com.example.diet_comment.service.ImageService;



@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

	private String imageSavePath;
	
	@Override
	public String uploadImageById(Integer Id,MultipartFile image,String code) {
        if(code.equals( "user")){
            imageSavePath = "./images/users/";
        }else if(code.equals("comment")){
            imageSavePath = "./images/comments/";
        } else if (code.equals("post")) {
            imageSavePath = "./images/posts/";
        }
        try {
			image.transferTo(new File(imageSavePath + Id + ".jpg"));
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return imageSavePath + Id + ".jpg";
	}
}
