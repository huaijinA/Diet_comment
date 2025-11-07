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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

	// 本地存储基础路径（需与配置的静态资源路径一致）
// 替换原有的 basePath，改为项目相对路径（存储到 src/main/resources/static/images/）
	private final String basePath = System.getProperty("user.dir") + "/src/main/resources/static/images/";



	// 服务器基础 URL（本地测试用 localhost，部署到云服务器改为云服务器 IP）
	@Value("${server.base-url:http://localhost:8080}")
	private String serverBaseUrl;

	@Override
	public String uploadImageById(Integer imageableId, MultipartFile image, String imageableType) {
		// 1. 参数校验
		if (imageableId == null || imageableId <= 0 || image == null || image.isEmpty()) {
			return null;
		}
		if (!isValidImageableType(imageableType)) {
			return null;
		}

		try {
			// 2. 处理文件信息：生成唯一文件名 + 保留原扩展名
			String originalFilename = image.getOriginalFilename();
			String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			String uniqueFileName = imageableId + "_" + UUID.randomUUID().toString().replace("-", "") + extension;

			// 3. 构建存储路径（按业务类型分目录）
			String storageDir = basePath + imageableType + "/";
			File dir = new File(storageDir);
			if (!dir.exists()) {
				dir.mkdirs(); // 递归创建目录
			}

			// 4. 保存图片到本地
			File targetFile = new File(storageDir + uniqueFileName);
			image.transferTo(targetFile);

			// 5. 生成可访问的 URL
			String imageUrl = serverBaseUrl + "/images/" + imageableType + "/" + uniqueFileName;

			// 6. 构建 Image 实体，存入数据库
			Image imageEntity = new Image();
			imageEntity.setImageableType(imageableType);
			imageEntity.setImageableId(imageableId);
			imageEntity.setImageUrl(imageUrl);
			imageEntity.setFileName(originalFilename);
			imageEntity.setFileSize(image.getSize());

			baseMapper.insert(imageEntity); // 存入数据库

			return imageUrl; // 返回 URL 给前端
		} catch (IOException e) {
			e.printStackTrace();
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
			if (!image.isEmpty()) {
				String url = uploadImageById(imageableId, image, imageableType);
				if (url != null) {
					imageUrls.add(url);
				}
			}
		}
		return imageUrls;
	}


	/**
	 * 根据业务类型和ID获取图片URL列表
	 * 对应接口文档中的 4.1获取图片 接口
	 */
	@Override
	public List<String> getImagesByTypeAndId(String imageableType, Integer imageableId) {
		// 参数校验
		if (imageableType == null || imageableType.trim().isEmpty() || imageableId == null || imageableId <= 0) {
			return new ArrayList<>();
		}

		try {
			// 构建查询条件
			QueryWrapper<Image> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("imageable_type", imageableType)
					.eq("imageable_id", imageableId)
					.orderByAsc("created_at"); // 按创建时间排序

			// 执行查询
			List<Image> imageList = this.list(queryWrapper);

			// 提取图片URL列表
			List<String> imageUrls = new ArrayList<>();
			for (Image image : imageList) {
				if (image.getImageUrl() != null && !image.getImageUrl().isEmpty()) {
					imageUrls.add(image.getImageUrl());
				}
			}

			return imageUrls;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	// 校验关联实体类型是否合法（对应接口规范中的 post/comment/shop/user）
	private boolean isValidImageableType(String type) {
		return "post".equals(type) || "comment".equals(type) || "shop".equals(type) || "user".equals(type);
	}
}