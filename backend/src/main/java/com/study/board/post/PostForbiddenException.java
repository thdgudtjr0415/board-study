package com.study.board.post;

public class PostForbiddenException extends RuntimeException {
    public PostForbiddenException() {
        super("본인이 쓴 글만 수정하거나 삭제할 수 있습니다.");
    }
}