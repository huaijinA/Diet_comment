package com.example.diet_comment.exception;

import com.example.diet_comment.model.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public Result handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        return Result.error("上传文件太大，已超过服务器允许的最大尺寸");
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleMultipartException(MultipartException ex) {
        return Result.error("文件上传失败：" + ex.getMessage());
    }
}