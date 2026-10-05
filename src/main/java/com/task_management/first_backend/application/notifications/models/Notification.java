package com.task_management.first_backend.application.notifications.models;

import com.task_management.first_backend.application.notifications.enums.NotificationType;
import com.task_management.first_backend.application.users.models.User;

import com.task_management.first_backend.application.users.models.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Builder
@Getter
@Setter
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private User actor;

    private String title;

    private String message;

    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private NotificationType type = NotificationType.OTHERS;

    private Long workspaceId;
    private Long channelId;
    private Long dmThreadId;

    private boolean isDispatched = false;
    private Date readAt;

    private LocalDateTime lastNotifiedAt;

    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
}
