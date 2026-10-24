package com.study.board.post;

import com.study.board.post.dto.PostResponse;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post save(Post post);          // ① 저장한 글(번호가 붙은 것)을 돌려준다
    Optional<Post> findById(Long id);        // ③ 번호로 찾는다. 없을 수도 있다
    void deleteById(Long id);

    Optional<Post> findByIdForUpdate(Long id);

    List<PostResponse> findAllWithAuthor();
    Optional<PostResponse> findResponseById(Long id);
}