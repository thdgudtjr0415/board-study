package com.study.board.comment.dto;

import com.study.board.comment.Comment;

// DTO는 값을 나르기만 해서 record 사용 (생성자/getter 자동, 필드 불변)
public record CommentResponse(Long id, String content, Long memberId, Long postId, String authorName) {
    public static CommentResponse from(Comment comment, String authorName ){
        // 응답에 줄 필드만 골라 담는 변환을 여기 모아 둠
        return new CommentResponse(comment.getId(), comment.getContent(), comment.getMemberId(), comment.getPostId(),authorName);
    }
}
