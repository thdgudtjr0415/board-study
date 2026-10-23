package com.study.board.comment;

public class CommentForbiddenException extends RuntimeException{
    public CommentForbiddenException(){
        super("본인이 쓴 댓글만 수정하거나 삭제할 수 있습니다.");
    }
}
