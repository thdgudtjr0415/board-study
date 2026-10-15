package com.study.board.post;

import com.study.board.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public PostService(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }
    // 생성
    public Post create(String title, String content, Long memberId){
        log.info("글 생성 요청 memberId = {}", memberId);

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
    // JPA로 읽은 객체만 JPA가 수정 가능, 여기서 그냥 일반 MyBatis로 post가져오면 응답은 될지라도 수정은 안됨
    @Transactional
    public Post update(Long id, String title, String content){
        Post post = postRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        post.update(title, content);
        return post;
    }
}
