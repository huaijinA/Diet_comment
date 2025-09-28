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

	@Value("${image.save.path}")
	private String imageSavePath;
	
	@Override
	public String uploadImage(Integer userId,MultipartFile image) {
		try {
			image.transferTo(new File(imageSavePath + userId + ".jpg"));
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return imageSavePath + userId + ".jpg";
	}
}
