package com.study.board.post;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

// @Mapper: 이 인터페이스의 구현체는 직접 쓰지 않는다. MyBatis 가 XML 을 보고 대신 만들어 준다(프록시).
// 연결 규칙: XML 의 namespace = 이 인터페이스의 패키지 포함 이름, <select id> = 아래 메소드 이름
@Mapper
public interface PostMapper {
    // 메소드 이름이 XML 의 id 와 다르면 "Invalid bound statement (not found)" 에러가 난다
    List<Post> findAll();
    Optional<Post> findById(Long id);

}
