package com.task_management.first_backend.application.dto.notifications;

import com.task_management.first_backend.application.enums.NotificationType;
import com.task_management.first_backend.application.models.Notification;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZoneId;

@Data
public class NotificationDTO {
    private Long id;

    private String title;
    private String message;
    private NotificationType type;
    private boolean read;
    private LocalDate createdAt;
    private String actorName;

    public NotificationDTO(Notification notification){
        setId(notification.getId());
        setTitle(notification.getTitle());
        setMessage(notification.getMessage());
        setRead(notification.isRead());
        setCreatedAt(notification.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        setActorName(notification.getUser().getFullName());
    }
}
