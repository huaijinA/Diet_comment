package com.example.diet_comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.diet_comment.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// ImageService.java
public interface ImageService extends IService<Image> {
    // 通用多图上传方法
    List<String> uploadMultiImages(Integer id, MultipartFile[] images, String code);

    // 单图上传方法
    String uploadImageById(Integer Id,MultipartFile image,String code);

    //根据业务类型和ID获取图片URL列表
    List<String> getImagesByTypeAndId(String imageableType, Integer imageableId);





}