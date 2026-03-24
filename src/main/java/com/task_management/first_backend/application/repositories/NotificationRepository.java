package com.task_management.first_backend.application.repositories;

import com.task_management.first_backend.application.enums.NotificationType;
import com.task_management.first_backend.application.models.Notification;
import com.task_management.first_backend.application.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUser(User user, Pageable pageable);
    Page<Notification> findByUserAndIsRead(User user, boolean isRead, Pageable pageable);
    long countByUser(User user);
    long countByUserAndIsRead(User user, boolean isRead);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user = :user")
    void markAllAsRead( @Param("user") User user);

    @Query("""
        SELECT n
        FROM Notification n
        WHERE n.isDispatched = false
    """)
    List<Notification> getNonDispatchedNotifications(Pageable pageable);

    @Query("""
        SELECT n
        FROM Notification n
        WHERE n.isDispatched = false
        AND n.user = :user
    """)
    List<Notification> findUndispatchedByUser(@Param("user") User user);

    Notification findByUserAndTypeAndLastNotifiedAt(User user, NotificationType type, LocalDate date);

    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.user = :user
            AND n.type = :type
            AND n.lastNotifiedAt BETWEEN :startDate AND :endDate
            """)
    Notification findTodayNotification(
            @Param("user") User user,
            @Param("type") NotificationType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
