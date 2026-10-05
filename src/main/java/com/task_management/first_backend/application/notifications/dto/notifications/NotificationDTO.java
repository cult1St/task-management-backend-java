package com.task_management.first_backend.application.notifications.dto.notifications;

import com.task_management.first_backend.application.notifications.enums.NotificationType;
import com.task_management.first_backend.application.notifications.models.Notification;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Long userId;
    private boolean read;
    private Date createdAt;
    private String actorName;
    private Long workspaceId;
    private Long channelId;
    private Long dmThreadId;

    public NotificationDTO(Notification notification) {
        setId(notification.getId());
        setTitle(notification.getTitle());
        setUserId(notification.getUser().getId());
        setMessage(notification.getMessage());
        setType(notification.getType());
        setRead(notification.isRead());
        if (notification.getCreatedAt() != null) {
            setCreatedAt(notification.getCreatedAt());
        }
        if (notification.getActor() != null) {
            setActorName(notification.getActor().getFullName());
        }
        setWorkspaceId(notification.getWorkspaceId());
        setChannelId(notification.getChannelId());
        setDmThreadId(notification.getDmThreadId());
    }
}
