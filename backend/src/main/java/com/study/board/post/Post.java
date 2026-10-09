package com.study.board.post;

import lombok.Getter;

@Getter
public class Post {
    private Long id;
    private String title;
    private String content;

    private void validate(String title, String content){
        if(title == null || title.isBlank() || content == null || content.isBlank()){
            throw new IllegalArgumentException("제목 혹은 내용이 비었습니다.");
        }
    }

    public Post(Long id, String title, String content) {
        validate(title, content);
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content){
        validate(title, content);
        this.title = title;
        this.content = content;
    }

}
