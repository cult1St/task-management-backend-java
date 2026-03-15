package com.task_management.first_backend.application.repositories;

import com.task_management.first_backend.application.models.Notification;
import com.task_management.first_backend.application.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUser(User user, Pageable pageable);
    Page<Notification> findByUserAndIsRead(User user, boolean isRead, Pageable pageable);
    long countByUser(User user);
    long countByUserAndIsRead(User user, boolean isRead);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user = :user")
    void markAllAsRead( @Param("user") User user);
}
