package com.study.board.post;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MemoryPostRepository implements PostRepository{
    private final List<Post> store = new ArrayList<>();
    private Long sequence = 1L;

    @Override
    public Post save(Post post) {
        Long id = sequence++;
        String title = post.getTitle();
        String content = post.getContent();
        Long memberId = post.getMemberId();

        Post saved = new Post(id, title, content, memberId);
        store.add(saved);
        System.out.println("게시글이 저장되었습니다.");
        return saved;
    }

    @Override
    public List<Post> findAll() {
        System.out.println("모든 글을 불러 왔습니다.");
        return store;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return store.stream()                    // 리스트를 "하나씩 흘려보내는 통로"로 바꾼다
                .filter(post -> post.getId().equals(id))   // 조건에 맞는 것만 통과시킨다
                .findFirst();                    // 통과한 것 중 첫 번째를 Optional로 돌려준다
    }
    @Override
    public void deleteById(Long id){
        store.removeIf(post -> post.getId().equals(id));
    }
}
