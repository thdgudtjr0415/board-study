package com.study.board.notification.dto;


import java.time.LocalDateTime;

public record NotificationResponse(
                                    Long id,
                                    Long postId,
                                    Long commentId,
                                    String commenterName,
                                    String commentPreview,
                                    Boolean isRead,
                                    LocalDateTime createdAt) {
}
