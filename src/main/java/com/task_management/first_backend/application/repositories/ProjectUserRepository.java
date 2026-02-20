package com.task_management.first_backend.application.repositories;

import com.task_management.first_backend.application.enums.ProjectUserStatus;
import com.task_management.first_backend.application.models.ProjectUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    public Page<ProjectUser> findByProjectId(Long projectId, Pageable pageable);
    public Page<ProjectUser> findByProjectIdAndAssignedBy(Long projectId, Long assignedBy, Pageable pageable);
    public Page<ProjectUser> findByProjectIdAndStatus(Long projectId, ProjectUserStatus status, Pageable pageable);
    public Page<ProjectUser> findByProjectIdAndStatusAndAssignedBy(Long projectId, ProjectUserStatus status, Long assignedBy, Pageable pageable);
}
