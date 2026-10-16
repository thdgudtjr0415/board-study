package com.study.board.post;

import java.util.List;
import java.util.Optional;

// @Repository 불필요: Spring Data가 JpaRepository 상속 인터페이스의 구현 객체를 자동 생성해 빈으로 등록함
public interface PostRepository {
    Post save(Post post);          // ① 저장한 글(번호가 붙은 것)을 돌려준다
    List<Post> findAll();                // ② 글 전체를 돌려준다
    Optional<Post> findById(Long id);        // ③ 번호로 찾는다. 없을 수도 있다
    void deleteById(Long id);

    Optional<Post> findByIdForUpdate(Long id);
}