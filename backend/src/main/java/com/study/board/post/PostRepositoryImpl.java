package com.study.board.post;

import com.study.board.post.dto.PostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//PostController
//   ↓ 호출
//PostService  ──→  MemberRepository (회원 확인)
//   ↓ 호출
//PostRepository   ← 인터페이스(약속)
//   ↓ 실제 구현
//PostRepositoryImpl  (지금의 MyBatisPostRepository)
//   ├─ 읽기  → PostMapper         → PostMapper.xml → DB
//   └─ 쓰기  → PostJpaRepository  → (JPA/Hibernate)  → DB

//@Primary 구현체 2개 일때 사용하면 이걸 먼저 읽음
@Repository
public class PostRepositoryImpl implements PostRepository{
    private final PostJpaRepository postJpaRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostRepositoryImpl(PostJpaRepository postJpaRepository, PostMapper postMapper) {
        this.postJpaRepository = postJpaRepository;
        this.postMapper = postMapper;
    }

    // JPA
    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postMapper.findById(id);
    }

    // JPA
    @Override
    public void deleteById(Long id) {
        postJpaRepository.deleteById(id);
    }
    // update
    @Override
    public Optional<Post> findByIdForUpdate(Long id) {
        return postJpaRepository.findById(id);
    }

    // 아래 두개는 작성자 추가 때문에 작성
    @Override
    public List<PostResponse> findAllWithAuthor() {
        return postMapper.findAllWithAuthor();
    }

    @Override
    public Optional<PostResponse> findResponseById(Long id) {
        return postMapper.findResponseById(id);
    }
}
