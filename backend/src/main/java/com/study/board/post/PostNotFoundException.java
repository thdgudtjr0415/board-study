package com.study.board.post;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long id){
        super("글을 찾을 수 없습니다: " + id);
    }
}
