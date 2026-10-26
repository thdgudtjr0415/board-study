package com.study.board.notification;

import com.study.board.notification.dto.NotificationResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {
    List<NotificationResponse> findByMemberId(Long memberId);
    Long countUnread(Long memberId);
}
