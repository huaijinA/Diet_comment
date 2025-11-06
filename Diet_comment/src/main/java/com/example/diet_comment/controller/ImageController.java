package com.example.diet_comment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.diet_comment.model.Result;
import com.example.diet_comment.service.ImageService;
import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    /**
     * 单文件/多文件上传合并接口
     * 适配 Result 类现有方法：仅支持 success()、success(data)、error(message)
     */
    @PostMapping("/upload") // 统一接口路径：/images/upload
    public Result uploadImages(
            @RequestParam Integer id, // 关联实体ID（如帖子ID、评论ID）
            @RequestParam String code, // 业务类型（user/comment/post/shop）
            @RequestParam("files") MultipartFile[] files) { // 统一接收单/多文件

        // 1. 基础参数校验
        if (id == null || id <= 0) {
            return Result.error("关联ID不合法（必须为正数）");
        }
        if (!isValidCode(code)) {
            return Result.error("类型标识错误（仅支持user/comment/post/shop）");
        }
        if (files == null || files.length == 0) {
            return Result.error("未选择任何图片文件");
        }

        // 2. 自动适配单/多文件处理（核心逻辑）
        try {
            if (files.length == 1) {
                // 单文件处理
                MultipartFile singleFile = files[0];
                if (singleFile.isEmpty()) {
                    return Result.error("单文件上传失败：文件为空");
                }
                String imageUrl = imageService.uploadImageById(id, singleFile, code);
                return imageUrl != null ?
                        Result.success(imageUrl) : // 仅返回 data（图片URL），message 默认为 "Success"
                        Result.error("单文件上传失败");
            } else {
                // 多文件处理
                List<String> imageUrls = imageService.uploadMultiImages(id, files, code);
                return !imageUrls.isEmpty() ?
                        Result.success(imageUrls) : // 仅返回 data（URL列表），message 默认为 "Success"
                        Result.error("多文件上传失败");
            }
        } catch (Exception e) {
            return Result.error("上传异常：" + e.getMessage());
        }
    }


    /**
     * 4.1 获取图片接口 - 根据文档规范实现
     * URL: /imageMap
     * 方法: GET
     * 请求格式: application/json
     */
    @GetMapping("/imageMap")
    public Result getImages(
            @RequestParam("imageableType") String imageableType,
            @RequestParam("imageableId") Integer imageableId) {

        // 参数校验
        if (imageableType == null || imageableType.trim().isEmpty()) {
            return Result.error("图片类型不能为空");
        }
        if (imageableId == null || imageableId <= 0) {
            return Result.error("图片关联ID不合法");
        }

        // 校验业务类型是否合法（根据文档规范）
        if (!isValidCode(imageableType)) {
            return Result.error("不支持的图片类型，仅支持post/comment/shop/user");
        }

        try {
            // 调用服务层获取图片URL列表
            List<String> imageUrls = imageService.getImagesByTypeAndId(imageableType, imageableId);

            // 根据文档规范返回结果
            if (imageUrls.isEmpty()) {
                return Result.success(null); // data为null，message提示信息
            } else {
                return Result.success(imageUrls); // data为图片URL数组
            }
        } catch (Exception e) {
            return Result.error("获取图片失败：" + e.getMessage());
        }
    }


    // 校验业务类型是否合法
    private boolean isValidCode(String code) {
        return "user".equals(code) || "comment".equals(code) || "post".equals(code) || "shop".equals(code);
    }
}