package com.study.board.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    public Post create(String title, String content){
        System.out.println("[Service] title=" + title + ", content=" + content);

        Post newPost = new Post(null, title, content);
        Post saved = postRepository.save(newPost);
        return saved;
    }
    public List<Post> findAll() {
        return postRepository.findAll();
    }
    public Post findById(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }
    public void delete(Long id){
        Post post = findById(id);    // 없으면 PostNotFoundException
        postRepository.deleteById(post.getId());
    }
    public Post update(Long id, String title, String content){
        Post post = findById(id);
        post.update(title, content);
        return post;
    }

}
