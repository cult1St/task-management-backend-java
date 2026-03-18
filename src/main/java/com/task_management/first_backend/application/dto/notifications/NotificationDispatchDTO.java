package com.task_management.first_backend.application.dto.notifications;

import com.task_management.first_backend.application.models.Notification;
import lombok.Data;

@Data
public class NotificationDispatchDTO {
    private String title;
    private String message;

    public NotificationDispatchDTO(Notification notification){
        setTitle(notification.getTitle());
        setMessage(notification.getMessage());
    }
}
