package com.task_management.first_backend.application.repositories;

import com.task_management.first_backend.application.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByCreatedBy(Long userId, Pageable pageable);
    Page<Task> findByAssignedTo(Long userId, Pageable pageable);
    Page<Task> findByCreatedByIdOrAssignedToId(
            Long createdById,
            Long assignedToId,
            Pageable pageable
    );
    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    @Query("""
            SELECT t
            FROM Task t
            WHERE t.dueDate >= :startDate
            AND t.dueDate <= :endDate
            """)
    List<Task> getTasksWithinDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(t)
            FROM Task t
            WHERE t.dueDate >= :startDate
            AND t.dueDate <= :endDate
            """)
    long getTasksCountWithinDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
