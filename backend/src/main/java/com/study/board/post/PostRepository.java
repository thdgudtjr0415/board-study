package com.study.board.post;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository {
    Post save(Post post);          // ① 저장한 글(번호가 붙은 것)을 돌려준다
    List<Post> findAll();                // ② 글 전체를 돌려준다
    Optional<Post> findById(Long id);        // ③ 번호로 찾는다. 없을 수도 있다
    void deleteById(Long id);
}