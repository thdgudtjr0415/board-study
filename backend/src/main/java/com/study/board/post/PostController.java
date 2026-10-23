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
        Post newPost = postService.create(request.title(), request.content(), memberId);
        PostResponse response = PostResponse.from(newPost);
        return response;
    }

    @GetMapping
    public List<PostResponse> list(){
        List<Post> allPost = postService.findAll();

        // [학습 메모] 같은 일을 for문으로 쓰면 (stream과 결과가 같음)
        //  List<PostResponse> result = new ArrayList<>();
        //  for (Post post : allPost) {
        //      result.add(PostResponse.from(post));
        //  }
        //  return result;

        return allPost.stream()
                .map(PostResponse :: from)
                .toList();
    }
    @GetMapping("/{id}")
    public PostResponse get(@PathVariable Long id){
        Post post = postService.findById(id);
        return PostResponse.from(post);
    }

    @PutMapping("/{id}")
    public PostResponse update(
            @PathVariable Long id,
            @AuthenticationPrincipal Long memberId,
            @RequestBody PostUpdateRequest request){
        Post post = postService.update(id,memberId, request.title(), request.content());
        return PostResponse.from(post);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal Long memberId){
        postService.delete(id, memberId);
    }
}
