package com.task_management.first_backend.application.repositories;

import com.task_management.first_backend.application.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByCreatedBy(Long userId, Pageable pageable);
    Page<Task> findByAssignedTo(Long userId, Pageable pageable);
    Page<Task> findByCreatedByIdOrAssignedToId(
            Long createdById,
            Long assignedToId,
            Pageable pageable
    );
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
}
