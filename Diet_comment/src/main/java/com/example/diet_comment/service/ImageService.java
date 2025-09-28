package com.example.diet_comment.service;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.diet_comment.model.Image;

public interface ImageService extends IService<Image> {
    String uploadImageById(Integer Id,MultipartFile image,String code);

}