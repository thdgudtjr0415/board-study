package com.study.board.comment;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "post_id")
    private Long postId;

    protected Comment() {}
    private void validate(String content) {
        if(content == null || content.isBlank()){
            throw new IllegalArgumentException("댓글에 내용이 비었습니다.");
        }
    }

    public Comment(Long id, String content, Long memberId, Long postId){
        validate(content);
        if(memberId == null || postId == null){
            throw new IllegalArgumentException("작성자 혹은 게시글이 없습니다.");
        }
        this.id = id;
        this.content = content;
        this.memberId = memberId;
        this.postId = postId;
    }

    public void update(String content){
        validate(content);
        this.content = content;
    }
}
