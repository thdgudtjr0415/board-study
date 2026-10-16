package com.study.board.member.dto;

import com.study.board.member.Member;

// DTO는 값을 나르기만 해서 record 사용 (생성자/getter 자동, 필드 불변)
public record MemberResponse(Long id, String loginId, String name) {
    public static MemberResponse from(Member member){
        // Entity를 그대로 내보내면 pwd가 노출되므로, 응답에 줄 필드만 골라 담는 변환을 여기 모아 둠
        return new MemberResponse(member.getId(), member.getLoginId(), member.getName());
    }
}
