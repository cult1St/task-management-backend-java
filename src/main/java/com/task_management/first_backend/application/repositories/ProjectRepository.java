package com.task_management.first_backend.application.repositories;

import com.task_management.first_backend.application.enums.ProjectStatus;
import com.task_management.first_backend.application.models.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    public Page<Project> findByUserId(Long userId, Pageable pageable);
    public Page<Project> findByStatus(ProjectStatus status, Pageable pageable);
    public Page<Project> findByUserIdAndStatus(Long userId, ProjectStatus status, Pageable pageable);
}
