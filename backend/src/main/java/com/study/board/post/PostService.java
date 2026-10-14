package com.study.board.post;

import com.study.board.member.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostJpaRepository postJpaRepository;

    @Autowired
    public PostService(PostRepository postRepository, MemberRepository memberRepository, PostJpaRepository postJpaRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.postJpaRepository = postJpaRepository;
    }
    // 생성
    public Post create(String title, String content, Long memberId){
        System.out.println("[Service] title=" + title + ", content=" + content);

        Post newPost = new Post(null, title, content, memberId);
        if(!memberRepository.existsById(memberId)){
            throw new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId);
        }
        Post saved = postRepository.save(newPost);
        return saved;
    }
    // 목록
    public List<Post> findAll() {
        return postRepository.findAll();
    }
    // ID 찾기
    public Post findById(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }
    // 삭제
    public void delete(Long id){
        Post post = findById(id);    // 없으면 PostNotFoundException
        postRepository.deleteById(post.getId());
    }

    // update
    @Transactional
    public Post update(Long id, String title, String content){
        Post post = postRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        post.update(title, content);
        return post;
    }
}
