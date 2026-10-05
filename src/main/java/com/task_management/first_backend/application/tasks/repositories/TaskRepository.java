package com.task_management.first_backend.application.tasks.repositories;

import com.task_management.first_backend.application.tasks.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

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
            SELECT t FROM Task t
            WHERE t.project.workspace.id = :workspaceId
            AND t.assignedTo.id = :userId
            AND t.status <> :doneStatus
            ORDER BY t.dueDate ASC
            """)
    Page<Task> findOpenTasksByWorkspaceAndAssignee(
            @Param("workspaceId") Long workspaceId,
            @Param("userId") Long userId,
            @Param("doneStatus") com.task_management.first_backend.application.tasks.enums.TaskStatus doneStatus,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(t) FROM Task t
            WHERE t.project.workspace.id = :workspaceId
            AND t.assignedTo.id = :userId
            AND t.status <> :doneStatus
            """)
    long countOpenTasksByWorkspaceAndAssignee(
            @Param("workspaceId") Long workspaceId,
            @Param("userId") Long userId,
            @Param("doneStatus") com.task_management.first_backend.application.tasks.enums.TaskStatus doneStatus
    );

    @Query("""
            SELECT t
            FROM Task t
            WHERE t.dueDate >= :startDate
            AND t.dueDate <= :endDate
            """)
    Page<Task> getTasksWithinDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("""
            SELECT t
            FROM Task t
            WHERE t.dueDate >= :date
            """)
    Page<Task> getTasksAfterDate(
            @Param("date") LocalDateTime dateTime,
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
