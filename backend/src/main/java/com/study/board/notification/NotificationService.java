package com.study.board.notification;

import com.study.board.notification.dto.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }
    // ① 글 작성자와 댓글 작성자가 같으면 아무것도 안 하고 return
    // ② 다르면 new Notification(null, postAuthorId, postId, commentId)를 save
    // ③ 저장하다 예외가 나면 log로 남기고 삼켜요 (try/catch)
    public void notifyNewComment(Long postAuthorId, Long commenterId, Long postId, Long commentId){
        // 비교 해서 같으면 바로 리턴, 그 이유는 이 메서드는 댓글의 작성자와 post의 작성자가 다를때를 위해 알림을 만드는 것
        if(postAuthorId.equals(commenterId)) return;

        try{
            Notification newNotification = new Notification(null, postAuthorId, postId, commentId);
            notificationRepository.save(newNotification);
        }catch (Exception e){
            log.warn("알림 저장 실패 postAuthorId : {}, commenterId : {}, postId : {}, commentId : {}",
                    postAuthorId, commenterId, postId, commentId, e);
        }
    }

    // 내가 받은 알림 목록
    public List<NotificationResponse> findByMemberId(Long memberId){
        return notificationMapper.findByMemberId(memberId);
    }

    // 1) 알림을 찾는다 (없으면 404) 2) 내 알림인지 확인한다 (남의 것이면 403) 3) 읽음으로 바꾼다
    // 알림 읽음 표시
    @Transactional
    public void markAsRead(Long id, Long memberId){
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        if(!notification.getMemberId().equals(memberId)){
            throw new NotificationForbiddenException();
        }
        notification.markAsRead();
    }

    // 안읽은 알림 개수
    public long countUnread(Long memberId){
        return notificationMapper.countUnread(memberId);
    }
}
