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

    @Value("${app.image.base-path:./}")
    private String baseImagePath;
    
    private String imageSavePath;
    
    @Override
    public String uploadImageById(Integer Id, MultipartFile image, String code) {
        if(code.equals("user")){
            imageSavePath = "/images/users/";
        }else if(code.equals("comment")){
            imageSavePath = "/images/comments/";
        } else if (code.equals("post")) {
            imageSavePath = "/images/posts/";
        }

        // 确保目录存在
        File directory = new File(baseImagePath + imageSavePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = Id + ".jpg";
        String filePath = imageSavePath + fileName;
        
        try {
            image.transferTo(new File(baseImagePath + filePath));
            
            // 保存图片信息到数据库
            Image imageEntity = new Image();
            imageEntity.setUrl(filePath);
            imageEntity.setImageableId(Id);
            imageEntity.setImageableType(code);
            // 如果需要可以设置alt_text
            imageEntity.setAltText(fileName);
            
            // 使用MyBatis-Plus的save方法保存到数据库
            this.save(imageEntity);
            
            return filePath;
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
