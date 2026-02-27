package com.task_management.first_backend.application.repositories;

import com.task_management.first_backend.application.enums.ProjectUserStatus;
import com.task_management.first_backend.application.models.ProjectUser;
import com.task_management.first_backend.application.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
     Page<ProjectUser> findByProjectId(Long projectId, Pageable pageable);
     Page<ProjectUser> findByProjectIdAndAssignedBy(Long projectId, User assignedBy, Pageable pageable);
     Page<ProjectUser> findByAssignedBy(User assignedBy, Pageable pageable);
     Page<ProjectUser> findByAssignedTo(User assignedTo, Pageable pageable);
     Page<ProjectUser> findByProjectIdAndStatus(Long projectId, ProjectUserStatus status, Pageable pageable);
     Page<ProjectUser> findByProjectIdAndStatusAndAssignedBy(Long projectId, ProjectUserStatus status, User assignedBy, Pageable pageable);
}
