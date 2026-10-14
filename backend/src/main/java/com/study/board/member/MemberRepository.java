package com.study.board.member;

import org.springframework.data.jpa.repository.JpaRepository;

//@Repository -> JPA 상속 시에는 필요 없음 JpaRepository 에 이미 @Repository 어노테이션 붙어 있음
public interface MemberRepository extends JpaRepository<Member, Long> {
}
