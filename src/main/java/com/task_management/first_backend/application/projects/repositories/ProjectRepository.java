package com.task_management.first_backend.application.projects.repositories;

import com.task_management.first_backend.application.projects.enums.ProjectStatus;
import com.task_management.first_backend.application.projects.models.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByUserId(Long userId, Pageable pageable);

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    Page<Project> findByUserIdAndStatus(Long userId, ProjectStatus status, Pageable pageable);

    Page<Project> findByWorkspaceId(Long workspaceId, Pageable pageable);

    Page<Project> findByWorkspaceIdAndStatus(Long workspaceId, ProjectStatus status, Pageable pageable);

    long countByWorkspaceId(Long workspaceId);

    boolean existsByWorkspaceIdAndKeyIgnoreCase(Long workspaceId, String key);
}
