package com.study.board.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// @Repository 불필요: Spring Data가 JpaRepository 상속 인터페이스의 구현 객체를 자동 생성해 빈으로 등록함
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByLoginId(String loginId);
    // 없을 수도 있다는 걸 타입에 드러내려고 Member 대신 Optional<Member> (null 실수 방지)
    Optional<Member> findByLoginId(String loginId);
}
