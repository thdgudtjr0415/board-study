package com.study.board.member;

import org.springframework.data.jpa.repository.JpaRepository;

// @Repository 불필요: Spring Data가 JpaRepository 상속 인터페이스의 구현 객체를 자동 생성해 빈으로 등록함
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByLoginId(String loginId);
}
