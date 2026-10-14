package com.study.board.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary

@Repository
public class MyBatisPostRepository implements PostRepository{
    private final PostJpaRepository postJpaRepository;
    private final PostMapper postMapper;

    @Autowired
    public MyBatisPostRepository(PostJpaRepository postJpaRepository, PostMapper postMapper) {
        this.postJpaRepository = postJpaRepository;
        this.postMapper = postMapper;
    }

    // JPA
    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    // findAll은 그대로 돌려주기
    @Override
    public List<Post> findAll() {
        return postMapper.findAll();
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
}
