package com.study.board.notification;

public class NotificationForbiddenException extends RuntimeException{
    public NotificationForbiddenException() {
        super("본인에게 온 알림만 읽음 처리할 수 있습니다.");
    }
}
