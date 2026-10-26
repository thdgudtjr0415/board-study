package com.study.board.notification;

import com.study.board.notification.dto.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 알림 목록
    @GetMapping
    public List<NotificationResponse> list(@AuthenticationPrincipal Long memberId){
        return notificationService.findByMemberId(memberId);
    }

    // 알림 읽은 것
    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void read(@PathVariable Long id,
                     @AuthenticationPrincipal Long memberId){
        notificationService.markAsRead(id, memberId);
    }

    @GetMapping("/unread-count")
    public long countUnread(@AuthenticationPrincipal Long memberId){
        return notificationService.countUnread(memberId);
    }

}
