package com.study.board.comment;

import com.study.board.comment.dto.CommentRequest;
import com.study.board.comment.dto.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping 이건 주소가 두 종류라서 각 메서드에 전체 주소 씀
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED) // 201 생성
    public CommentResponse create(@PathVariable Long postId,
                                  @AuthenticationPrincipal Long memberId,
                                  @RequestBody CommentRequest request){
        // service 에서 memberId, postId, content 순으로 작성해서 그거 따라가기
        Comment newComment = commentService.create(memberId, postId, request.content());
        CommentResponse response = CommentResponse.from(newComment);
        return response;
    }
    @GetMapping("/posts/{postId}/comments")
    // 기본 200이라 굳이 ResponseStatus 안씀
    // 여기서 PathVariable 들 쓰는것들은 전무 url에 들어가서 쓰는거임
    public List<CommentResponse> list(@PathVariable Long postId){
        List<Comment> postByComment = commentService.findByPostId(postId);

//        이거랑 같음
//        List<CommentResponse> result = new ArrayList<>();
//        for (Comment comment : postByComment) {
//            result.add(CommentResponse.from(comment));
//        }
//        return result;

        return postByComment.stream()
                .map(CommentResponse::from) //.map(comment -> CommentResponse.from(comment)) 이거랑 같음
                .toList();
    }
    // 수정, 이것도 기본 200 이라 responseStatus안씀
    @PutMapping("/comments/{id}")
    public CommentResponse update(@PathVariable Long id,
                                  @AuthenticationPrincipal Long memberId,
                                  @RequestBody CommentRequest request){
        Comment comment = commentService.update(id, memberId, request.content());
        return CommentResponse.from(comment);
    }

    // 삭제
    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal Long memberId){
        commentService.delete(id, memberId);
    }
}
