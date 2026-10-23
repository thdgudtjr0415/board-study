package com.study.board.comment;

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


    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }
    // postId를 검증하는게 여러번 나와서 따로 뺌
    private void checkPostExists(Long postId){
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
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

    public Comment create(Long memberId, Long postId, String content){
        log.info("댓글 생성 요청 memberId : {}, postId : {}", memberId, postId); // content를 넣으면 너무 길어짐
        Comment newComment = new Comment(null, content, memberId, postId);
        // 글이 있는지 확인만 한다 (결과는 안 씀). 없으면 PostNotFoundException → 404.
        // 이 확인이 없으면 없는 글의 id로 저장하다 DB의 FK 오류가 나서 처리 안 된 500이 됨
        checkPostExists(postId);
        Comment saved = commentRepository.save(newComment);
        return saved;
    }
    // 목록
    public List<Comment> findByPostId(Long postId){
        checkPostExists(postId);
        return commentRepository.findByPostId(postId);
    }
    // 수정
    // 1) 댓글을 찾고 내 댓글인지 확인한다 (없으면 404, 남의 댓글이면 403)
    // 2) 내용을 바꾼다 (comment.update, 빈 내용이면 400)
    // 3) 수정된 댓글을 돌려준다
    @Transactional
    public Comment update(Long id, Long memberId, String content){
        Comment comment = findOwnedComment(id, memberId);
        comment.update(content);
        return comment;
    }

    // 삭제
    // 1) 댓글을 찾고 내 댓글인지 확인한다 (없으면 404, 남의 댓글이면 403)
    // 2) 삭제한다
    public void delete(Long id, Long memberId){
        Comment comment = findOwnedComment(id, memberId);
        commentRepository.delete(comment);
    }
}
