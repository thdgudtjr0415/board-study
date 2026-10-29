package com.study.board.notification;

import com.study.board.notification.dto.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SseEmitterRepository sseEmitterRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               NotificationMapper notificationMapper, SseEmitterRepository sseEmitterRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.sseEmitterRepository = sseEmitterRepository;
    }
    // ① 글 작성자와 댓글 작성자가 같으면 아무것도 안 하고 return
    // ② 다르면 new Notification(null, postAuthorId, postId, commentId)를 save
    // ③ 저장하다 예외가 나면 log로 남기고 삼켜요 (try/catch)
    public void notifyNewComment(Long postAuthorId, Long commenterId, Long postId, Long commentId){
        // 비교 해서 같으면 바로 리턴, 그 이유는 이 메서드는 댓글의 작성자와 post의 작성자가 다를때를 위해 알림을 만드는 것
        if(postAuthorId.equals(commenterId)) return;

        try{
            Notification newNotification = new Notification(null, postAuthorId, postId, commentId);
            Notification saved = notificationRepository.save(newNotification);
            send(postAuthorId, saved.getId());
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
    // 11번이 알림 화면에 접속할 때 호출된다: 통로를 하나 만들어 보관하고 돌려준다
    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);   // 통로 만들기, 제한 시간 1시간(밀리초)
        sseEmitterRepository.add(memberId, emitter);            // 보관소에 넣는다

        // 연결이 끝나면 보관소에서 뺀다 (안 빼면 죽은 통로가 쌓인다)
        emitter.onCompletion(() -> sseEmitterRepository.remove(memberId, emitter));   // 정상 종료
        emitter.onTimeout(() -> sseEmitterRepository.remove(memberId, emitter));      // 시간 초과
        emitter.onError(e -> sseEmitterRepository.remove(memberId, emitter));         // 오류

        try {
            // 연결 직후에 이벤트를 한 번 보낸다 (아무것도 안 보내면 연결이 열렸는지 확인이 안 되고, 일부 환경에서 에러가 난다)
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            sseEmitterRepository.remove(memberId, emitter);     // 보내다 실패하면 이 통로는 뺀다
        }
        return emitter;
    }
    // 그 회원의 모든 통로(탭)에 새 알림 신호를 보낸다
    private void send(Long memberId, Long notificationId) {
        // 그 회원의 통로 목록을 하나씩 돈다 (접속 안 했으면 빈 목록이라 아무 일도 안 한다)
        for (SseEmitter emitter : sseEmitterRepository.findByMemberId(memberId)) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notificationId));   // 이름과 내용
            } catch (IOException e) {
                sseEmitterRepository.remove(memberId, emitter);   // 끊긴 통로는 보관소에서 뺀다
            }
        }
    }

}
