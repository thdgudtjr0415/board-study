package com.study.board.post;

import com.study.board.comment.CommentRepository;
import com.study.board.member.Member;
import com.study.board.member.MemberRepository;
import com.study.board.post.dto.PostResponse;
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
    // CommentService 대신 Repository를 받음: 글 삭제 때 댓글을 지우는 일에는 지킬 규칙이 없고,
    // 서로 Service를 부르면 나중에 순환 참조가 날 수 있어서 (댓글에 규칙이 생기면 Service로 옮김)
    private final CommentRepository commentRepository; // 댓글 삭제 때문에 들어옴

    @Autowired
    public PostService(PostRepository postRepository,
                       MemberRepository memberRepository,
                       CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.commentRepository = commentRepository;
    }
    // 생성
    public PostResponse create(String title, String content, Long memberId){
        log.info("글 생성 요청 memberId = {}", memberId);

        Post newPost = new Post(null, title, content, memberId);
        // 회원이 있는지 확인하면서 이름도 같이 가져온다 (existsById는 true/false만 줘서 이름을 못 가져옴)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));
        Post saved = postRepository.save(newPost);
        return PostResponse.from(saved, member.getName());
    }
    // 목록
    public List<PostResponse> findAllWithAuthor() {
        return postRepository.findAllWithAuthor();
    }

    // ID 찾기
    public Post findById(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }
    // ID 찾기 -> 바뀐 PostResponse
    public PostResponse findResponseById(Long id){
        return postRepository.findResponseById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    // update
    // JPA로 읽은 객체만 JPA가 수정 가능, 여기서 그냥 일반 MyBatis로 post가져오면 응답은 될지라도 수정은 안됨
    @Transactional
    public PostResponse update(Long id, Long memberId, String title, String content){
        // 글이 없으면 PostNotFoundException (비었을 때만 예외를 만들도록 람다로 미룸)
        // 생성자에 id를 넘겨야 해서 ::new 는 못 쓰고 () -> new ... 로 씀
        Post post = postRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        if(!post.getMemberId().equals(memberId)){
            throw new PostForbiddenException();
        }
        post.update(title, content);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));
        return PostResponse.from(post,member.getName());
    }

    // 삭제
    // 주의! table간 부모 자식, 즉 게시글 안에 댓글이 있기 때문에 자식 먼저 지워야함
    @Transactional
    public void delete(Long id, Long memberId){
        Post post = findById(id);    // 서비스 안의 findById 호출, 없으면 PostNotFoundException
        if(!post.getMemberId().equals(memberId)){
            throw new PostForbiddenException();
        }
        commentRepository.deleteByPostId(post.getId());
        postRepository.deleteById(post.getId());
    }
}
