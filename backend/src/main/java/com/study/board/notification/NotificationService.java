package com.study.board.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    // ① 글 작성자와 댓글 작성자가 같으면 아무것도 안 하고 return
    // ② 다르면 new Notification(null, postAuthorId, postId, commentId)를 save
    // ③ 저장하다 예외가 나면 log로 남기고 삼켜요 (try/catch)
    public void notifyNewComment(Long postAuthorId, Long commenterId, Long postId, Long commentId){
        // 배교 해서 같으면 바로 리턴, 그 이유는 이 매서드는 댓글의 작성자와 post의 작성자가 다를때를 위해 알림을 만드는 것
        if(postAuthorId.equals(commenterId)) return;

        try{
            Notification newNotification = new Notification(null, postAuthorId, postId, commentId);
            notificationRepository.save(newNotification);
        }catch (Exception e){
            log.warn("알림 저장 실패 postAuthorId : {}, commenterId : {}, postId : {}, commentId : {}",
                    postAuthorId, commenterId, postId, commentId, e);
        }
    }
}
