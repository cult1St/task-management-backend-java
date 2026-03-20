package com.task_management.first_backend.application.services;


import com.task_management.first_backend.application.dto.notifications.NotificationDTO;
import com.task_management.first_backend.application.dto.notifications.NotificationDispatchDTO;
import com.task_management.first_backend.application.enums.NotificationType;
import com.task_management.first_backend.application.models.Notification;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.repositories.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

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
                .lastNotifiedAt(LocalDateTime.now())
                .build();

        repository.save(notification);

        return new NotificationDTO(notification);
    }

    public boolean checkForPrevSent(User user, NotificationType type, LocalDate date){
        Notification notification = repository.findByUserAndTypeAndLastNotifiedAt(user, type, date);
        return notification != null;
    }

    protected NotificationDTO mapToDTO(Notification notification){
        return new NotificationDTO(notification);
    }

    @Transactional
    public void processNotificationsSending(){
        Pageable pageable = PageRequest.of(0, 100);
        List<Notification> pendingNotifications = repository.getNonDispatchedNotifications(pageable);
        for(Notification notification: pendingNotifications){
            NotificationDispatchDTO dispatchDTO = new NotificationDispatchDTO(notification);
            messagingTemplate.convertAndSendToUser(
                    notification.getUser().getUsername(),
                    "/queue/notifications",
                    dispatchDTO
            );

            notification.setDispatched(true);
        }
        repository.saveAll(pendingNotifications);
    }
}
