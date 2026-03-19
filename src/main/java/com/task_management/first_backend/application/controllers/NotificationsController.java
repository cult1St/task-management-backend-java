package com.task_management.first_backend.application.controllers;

import com.task_management.first_backend.application.dto.SuccessResponse;
import com.task_management.first_backend.application.dto.invitations.InvitationDTO;
import com.task_management.first_backend.application.dto.notifications.NotificationCountDTO;
import com.task_management.first_backend.application.dto.notifications.NotificationDTO;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationsController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<SuccessResponse<Page<NotificationDTO>>> getReceivedInvitations(
            @AuthenticationPrincipal User authUser,
            @RequestParam(name = "unreadOnly", defaultValue = "false") String unreadOnly,
            @RequestParam(name = "limit", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        //set correctly params
        size = size > 0 ? size : 20;
        page = page > 0 ? (page - 1) : 0;

        //format type
        String type = !Objects.equals(unreadOnly, "true") ? "all" : "unread";
        Page<NotificationDTO> notifications = notificationService.getUserNotifications(
                authUser,
                type,
                page,
                size
        );

        return ResponseEntity.ok(
                SuccessResponse.of("Notifications fetched Successfully", notifications)
        );

    }

    @GetMapping("unread-count")
    public ResponseEntity<SuccessResponse<NotificationCountDTO>> getUnreadNotificationsCount(
            @AuthenticationPrincipal User authUser
    ){
        Long notificationsCount = notificationService.countUnreadNotifications(authUser);
        NotificationCountDTO countDTO = new NotificationCountDTO(notificationsCount);
        return ResponseEntity.ok(
                SuccessResponse.of("Notifications Count Fetched Successfully", countDTO)
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<SuccessResponse<NotificationDTO>> readNotification(
            @AuthenticationPrincipal User authUser,
            @PathVariable Long id
    ){
        NotificationDTO notification = notificationService.readNotification(id);
        return ResponseEntity.ok(
                SuccessResponse.of("Notification Read Successfully", notification)
        );
    }

    @PatchMapping("/read-all")
    public ResponseEntity<SuccessResponse<Boolean>> readAllNotifications(
            @AuthenticationPrincipal User authUser
    ){
        Boolean response = notificationService.markAllAsRead(authUser);
        return ResponseEntity.ok(
                SuccessResponse.of("Notification Read Successfully", response)
        );
    }

    public void sendNotifications(){

    }
}
