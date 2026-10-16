package com.study.board.post.dto;

import com.study.board.post.Post;
// id, title, content
// DTO는 값을 나르기만 해서 record 사용 (생성자/getter 자동, 필드 불변)
public record PostResponse(Long id, String title, String content, Long memberId) {
    public static PostResponse from(Post post){
        // 응답에 줄 필드만 골라 담는 변환을 여기 모아 둠
        return new PostResponse(post.getId(),post.getTitle(),post.getContent(), post.getMemberId());
    }
}
    