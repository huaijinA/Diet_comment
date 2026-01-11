// `src/main/java/com/example/diet_comment/service/Impl/ImageServiceImpl.java`
package com.example.diet_comment.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.diet_comment.mapper.ImageMapper;
import com.example.diet_comment.model.Image;
import com.example.diet_comment.service.ImageService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

    // 外部可写目录（相对路径：以程序启动目录为基准）
    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    // 服务器基础 URL
    @Value("${server.base-url:http://localhost:8080}")
    private String serverBaseUrl;

    @Override
    public String uploadImageById(Integer imageableId, MultipartFile image, String imageableType) {
        if (imageableId == null || imageableId <= 0 || image == null || image.isEmpty()) {
            return null;
        }
        if (!isValidImageableType(imageableType)) {
            return null;
        }

        try {
            String originalFilename = image.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFileName = imageableId + "_" + UUID.randomUUID().toString().replace("-", "") + extension;

            // ./uploads/{type}/
            Path storageDir = Paths.get(uploadDir, imageableType).toAbsolutePath().normalize();
            File dir = storageDir.toFile();
            if (!dir.exists() && !dir.mkdirs()) {
                return null;
            }

            File targetFile = storageDir.resolve(uniqueFileName).toFile();
            image.transferTo(targetFile);

            // 对外访问：/images/{type}/{file}
            String imageUrl = serverBaseUrl + "/images/" + imageableType + "/" + uniqueFileName;

            Image imageEntity = new Image();
            imageEntity.setImageableType(imageableType);
            imageEntity.setImageableId(imageableId);
            imageEntity.setImageUrl(imageUrl);
            imageEntity.setFileName(originalFilename);
            imageEntity.setFileSize(image.getSize());

            baseMapper.insert(imageEntity);
            return imageUrl;
        } catch (IOException e) {
            log.error("upload image failed", e);
            return null;
        }
    }

    @Override
    public List<String> uploadMultiImages(Integer imageableId, MultipartFile[] images, String imageableType) {
        List<String> imageUrls = new ArrayList<>();
        if (imageableId == null || imageableId <= 0 || images == null || images.length == 0) {
            return imageUrls;
        }
        if (!isValidImageableType(imageableType)) {
            return imageUrls;
        }

        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                String url = uploadImageById(imageableId, image, imageableType);
                if (url != null) {
                    imageUrls.add(url);
                }
            }
        }
        return imageUrls;
    }

    @Override
    public List<String> getImagesByTypeAndId(String imageableType, Integer imageableId) {
        if (imageableType == null || imageableType.trim().isEmpty() || imageableId == null || imageableId <= 0) {
            return new ArrayList<>();
        }

        try {
            QueryWrapper<Image> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("imageable_type", imageableType)
                    .eq("imageable_id", imageableId)
                    .orderByAsc("created_at");

            List<Image> imageList = this.list(queryWrapper);

            List<String> imageUrls = new ArrayList<>();
            for (Image img : imageList) {
                if (img.getImageUrl() != null && !img.getImageUrl().isEmpty()) {
                    imageUrls.add(img.getImageUrl());
                }
            }
            return imageUrls;
        } catch (Exception e) {
            log.error("get images failed", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteByTypeAndId(String imageableType, Integer imageableId) {
        if (imageableType == null || imageableType.trim().isEmpty() || imageableId == null || imageableId <= 0) {
            return;
        }

        try {
            QueryWrapper<Image> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("imageable_type", imageableType)
                    .eq("imageable_id", imageableId);

            List<Image> imagesToDelete = this.list(queryWrapper);
            if (imagesToDelete == null || imagesToDelete.isEmpty()) {
                return;
            }
            System.out.println("Deleting images: " + imagesToDelete.size());

            for (Image image : imagesToDelete) {
                String imageUrl = image.getImageUrl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    continue;
                }

                String urlPathSegment = "/images/";
                int pathStartIndex = imageUrl.indexOf(urlPathSegment);
                if (pathStartIndex == -1) {
                    continue;
                }

                // 取出 images/ 后的相对路径：{type}/{file}
                String relativePath = imageUrl.substring(pathStartIndex + urlPathSegment.length());
                Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(relativePath).normalize();
                File fileToDelete = filePath.toFile();

                if (fileToDelete.exists() && fileToDelete.isFile()) {
                    boolean ok = fileToDelete.delete();
                    if (!ok) {
                        log.warn("delete file failed: " + fileToDelete.getAbsolutePath());
                    }
                }
            }

            this.remove(queryWrapper);
        } catch (Exception e) {
            log.error("delete images failed"+ e);
        }
    }

    private boolean isValidImageableType(String type) {
        return "post".equals(type) || "comment".equals(type) || "shop".equals(type) || "user".equals(type);
    }
}
