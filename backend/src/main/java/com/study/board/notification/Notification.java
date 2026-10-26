package com.study.board.notification;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "is_read")
    private boolean isRead;

    protected Notification() {}

    // isRead 는 처음 알림이 항상 안읽음 (false) 이기때문에 생성자에서 뺌 기본 boolean은 false
    public Notification(Long id, Long memberId, Long postId, Long commentId){
        validate(memberId, postId, commentId);
        this.id = id;
        this.memberId = memberId;
        this.postId = postId;
        this.commentId = commentId;
    }

    private void validate(Long memberId, Long postId, Long commentId) {
        if(memberId == null) throw new IllegalArgumentException("받는사람이 없습니다.");
        if(postId == null) throw new IllegalArgumentException("게시글이 없습니다.");
        if(commentId == null) throw new IllegalArgumentException("댓글이 없습니다.");
    }

    public void markAsRead(){
        this.isRead = true;
    }
}
