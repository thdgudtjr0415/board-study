package com.study.board.comment;

import com.study.board.comment.dto.CommentResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<CommentResponse> findByPostIdWithAuthor(Long postId);
}
