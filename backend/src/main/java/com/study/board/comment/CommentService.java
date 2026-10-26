package com.study.board.comment;

import com.study.board.comment.dto.CommentResponse;
import com.study.board.member.Member;
import com.study.board.member.MemberRepository;
import com.study.board.notification.NotificationService;
import com.study.board.post.Post;
import com.study.board.post.PostNotFoundException;
import com.study.board.post.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;


    @Autowired
    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          CommentMapper commentMapper, MemberRepository memberRepository,
                          NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
        this.memberRepository = memberRepository;
        this.notificationService = notificationService;
    }

    // postId를 검증하는게 여러번 나와서 따로 뺌, 리턴도 Post로 변환, 즉 Post를 직접 쓴 작성자 찾으려고
    // 글이 있는지 확인하고, 있으면 그 글을 돌려준다. 없으면 404
    private Post findPost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        return post;
    }
    private Comment findOwnedComment(Long id, Long memberId){
        // 1) 댓글을 찾는다. 없으면 CommentNotFoundException (404)
        // 2) 댓글의 작성자(memberId)와 요청한 사람이 다르면 CommentForbiddenException (403)
        // 3) 통과하면 그 댓글을 돌려준다
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
        if(!comment.getMemberId().equals(memberId)){
            throw new CommentForbiddenException();
        }
        return comment;
    }

    // create에는 @Transactional이 없어서 save가 끝나는 순간 댓글은 이미 커밋. 그래서 뒤에서 알림이 실패해도 댓글은 그대로 남음
    public CommentResponse create(Long memberId, Long postId, String content){
        log.info("댓글 생성 요청 memberId : {}, postId : {}", memberId, postId); // content를 넣으면 너무 길어짐
        Comment newComment = new Comment(null, content, memberId, postId);
        // 글을 찾는다. 없으면 PostNotFoundException → 404.
        // 이 확인이 없으면 없는 글의 id로 저장하다 DB의 FK 오류가 나서 처리 안 된 500이 됨
        // 찾은 글은 글 작성자(post.getMemberId())를 알려고 아래 알림 호출에서 씀
        Post post = findPost(postId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 회원입니다: " + memberId));
        Comment saved = commentRepository.save(newComment);
        // 알림 기능
        // post.getMemberId() : 게시글 작성자, memberId : 댓글 작성자, postId : 게시글 번호, saved.getId() : 저장된 댓글의 id
        notificationService.notifyNewComment(post.getMemberId(), memberId, postId, saved.getId());
        return CommentResponse.from(saved, member.getName());
    }
    // 목록
    public List<CommentResponse> findByPostId(Long postId){
        findPost(postId);
        return commentMapper.findByPostIdWithAuthor(postId);
    }
    // 수정
    // 1) 댓글을 찾고 내 댓글인지 확인한다 (없으면 404, 남의 댓글이면 403)
    // 2) 내용을 바꾼다 (comment.update, 빈 내용이면 400)
    // 3) 수정된 댓글을 돌려준다
    @Transactional
    public CommentResponse update(Long id, Long memberId, String content){
        Comment comment = findOwnedComment(id, memberId);
        comment.update(content);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));
        return CommentResponse.from(comment, member.getName());
    }

    // 삭제
    // 1) 댓글을 찾고 내 댓글인지 확인한다 (없으면 404, 남의 댓글이면 403)
    // 2) 삭제한다
    public void delete(Long id, Long memberId){
        Comment comment = findOwnedComment(id, memberId);
        commentRepository.delete(comment);
    }
}
