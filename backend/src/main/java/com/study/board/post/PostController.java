package com.study.board.post;

import com.study.board.post.dto.PostCreateRequest;
import com.study.board.post.dto.PostResponse;
import com.study.board.post.dto.PostUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(@AuthenticationPrincipal Long memberId,
                               @RequestBody PostCreateRequest request){
        PostResponse response = postService.create(request.title(), request.content(), memberId);
        return response;
    }

    @GetMapping
    public List<PostResponse> list(){
        return postService.findAllWithAuthor();
    }

    // 게시글 상세
    @GetMapping("/{id}")
    public PostResponse get(@PathVariable Long id){
        PostResponse post = postService.findResponseById(id);
        return post;
    }

    @PutMapping("/{id}")
    public PostResponse update(
            @PathVariable Long id,
            @AuthenticationPrincipal Long memberId,
            @RequestBody PostUpdateRequest request){
        PostResponse post = postService.update(id,memberId, request.title(), request.content());
        return post;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal Long memberId){
        postService.delete(id, memberId);
    }
}
