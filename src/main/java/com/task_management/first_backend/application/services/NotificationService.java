package com.task_management.first_backend.application.services;


import com.task_management.first_backend.application.dto.notifications.NotificationDTO;
import com.task_management.first_backend.application.enums.NotificationType;
import com.task_management.first_backend.application.models.Notification;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.repositories.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository repository;

    public Page<NotificationDTO> getUserNotifications(User user, String type, int page, int limit){
        Pageable pageable = PageRequest.of(page, limit);

        Page<Notification> notifications = switch (type) {
            case "read" -> repository.findByUserAndIsRead(
                    user,
                    true,
                    pageable
            );
            case "unread" -> repository.findByUserAndIsRead(
                    user,
                    false,
                    pageable
            );
            default -> repository.findByUser(
                    user,
                    pageable
            );
        };
        return notifications.map(this::mapToDTO);

    }
    public NotificationDTO getNotification(Long id){
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification Not Found"));
        return new NotificationDTO(notification);
    }
    public NotificationDTO readNotification(Long id){
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification Not Found"));
        notification.setRead(true);
        notification.setReadAt(new Date());
        repository.save(notification);
        return new NotificationDTO(notification);
    }

    public Long countUnreadNotifications(User user){
        return repository.countByUserAndIsRead(user, false);
    }


    public boolean markAllAsRead(User user){
        repository.markAllAsRead(user);
        return true;
    }


    //static service to create notification
    public NotificationDTO createNotification(
            User user,
            String title,
            String message,
            NotificationType type,
            User actor
    ){
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .actor(actor)
                .build();

        repository.save(notification);

        return new NotificationDTO(notification);
    }

    protected NotificationDTO mapToDTO(Notification notification){
        return new NotificationDTO(notification);
    }

}
