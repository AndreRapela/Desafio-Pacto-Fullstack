package com.pacto.recrutamento.dto.notification;

import com.pacto.recrutamento.domain.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        boolean read,
        LocalDateTime createdAt
) {
    public NotificationResponse(Notification notification){
        this(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getReadAt() != null,
                notification.getCreatedAt()

        );
    }
}
