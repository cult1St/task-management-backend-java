package com.task_management.first_backend.application.services;


import com.task_management.first_backend.application.dto.notifications.NotificationDTO;
import com.task_management.first_backend.application.dto.notifications.NotificationDispatchDTO;
import com.task_management.first_backend.application.enums.NotificationType;
import com.task_management.first_backend.application.models.CustomUserDetails;
import com.task_management.first_backend.application.models.Notification;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.repositories.NotificationRepository;
import com.task_management.first_backend.application.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;

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

    public boolean checkForPrevSent(User user, NotificationType type, LocalDate today) {

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        Notification notification = repository.findTodayNotification(
                user,
                type,
                start,
                end
        );

        return notification != null;
    }

    protected NotificationDTO mapToDTO(Notification notification){
        return new NotificationDTO(notification);
    }

    @Transactional
    public void processNotificationsSending(){
        Pageable pageable = PageRequest.of(0, 100);
        List<Notification> pendingNotifications = repository.getNonDispatchedNotifications(pageable);
        System.out.println("Starting notifications processing");
        System.out.println("Connected users: " + userRegistry.getUsers());
        for(Notification notification: pendingNotifications){
            System.out.println("Starting process for notification. Id " + notification.getId() + " Title: " + notification.getTitle());
            NotificationDTO dispatchDTO = new NotificationDTO(notification);
//            messagingTemplate.convertAndSendToUser(
//                    notification.getUser().getUsername(),
//                    "/queue/notifications",
//                    dispatchDTO
//            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications",
                    dispatchDTO
            );

            System.out.println("Sending to user: " + notification.getUser().getUsername());

            notification.setDispatched(true);
            notification.setLastNotifiedAt(LocalDateTime.now());
        }
        repository.saveAll(pendingNotifications);
    }


    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        User user = resolveUser(accessor);
        if (user == null) {
            System.out.println("[WS EVENT] ❌ Still no user after fallback");
            return;
        }

        System.out.println("[WS EVENT] ✅ User: " + user.getUsername());

        if ("/user/queue/notifications".equals(accessor.getDestination())) {
            System.out.println("[WS EVENT] 🚀 Sending pending notifications");

            List<Notification> notifications =
                    repository.findUndispatchedByUser(user);

            for (Notification notification : notifications) {
                messagingTemplate.convertAndSendToUser(
                        user.getUsername(),
                        "/queue/notifications",
                        new NotificationDTO(notification)
                );

                notification.setDispatched(true);
            }

            repository.saveAll(notifications);
        }
    }

    private User resolveUser(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        User user = resolveUserFromPrincipal(principal);
        if (user != null) {
            return user;
        }

        Object sessionUser = accessor.getSessionAttributes().get("user");
        if (sessionUser instanceof Principal sessionPrincipal) {
            user = resolveUserFromPrincipal(sessionPrincipal);
            if (user != null) {
                return user;
            }
        }

        Object sessionUsername = accessor.getSessionAttributes().get("username");
        if (sessionUsername instanceof String username) {
            return userRepository.findByEmail(username);
        }

        return null;
    }

    private User resolveUserFromPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }

        if (principal instanceof Authentication authentication) {
            Object authPrincipal = authentication.getPrincipal();
            if (authPrincipal instanceof User user) {
                return user;
            }
            if (authPrincipal instanceof CustomUserDetails details) {
                return details.getUser();
            }
        }

        String username = principal.getName();
        if (username == null || username.isBlank()) {
            return null;
        }
        return userRepository.findByEmail(username);
    }
}
