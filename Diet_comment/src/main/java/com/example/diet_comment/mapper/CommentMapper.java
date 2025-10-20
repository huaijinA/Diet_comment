package com.example.diet_comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.diet_comment.model.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
}