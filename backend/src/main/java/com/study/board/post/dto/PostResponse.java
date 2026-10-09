package com.study.board.post.dto;

import com.study.board.post.Post;
// id, title, content

public record PostResponse(Long id, String title, String content) {
    public static PostResponse from(Post post){
        return new PostResponse(post.getId(),post.getTitle(),post.getContent());
    }
}
