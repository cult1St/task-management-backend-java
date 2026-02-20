package com.task_management.first_backend.application.dto.projects;

import com.task_management.first_backend.application.enums.ProjectUserStatus;
import com.task_management.first_backend.application.helpers.DateHelper;
import com.task_management.first_backend.application.models.ProjectUser;
import com.task_management.first_backend.application.models.User;
import lombok.Data;

@Data
public class ProjectMemberDTO {
    private Long id;
    private User user;
    private String role;
    private ProjectUserStatus status;
    private String requestedAt;
    private String acceptedAt;

    public ProjectMemberDTO(ProjectUser projectUser){
        setId(projectUser.getId());
        setUser(projectUser.getAssignedTo());
        setStatus(projectUser.getStatus());
        setRole(projectUser.getRole());
        setRequestedAt(DateHelper.formatDateToMD(projectUser.getCreatedAt()));
        setAcceptedAt(DateHelper.formatDateToMD(projectUser.getAcceptedAt()));
    }
}
