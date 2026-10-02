package com.task_management.first_backend.application.dto.projects;

import com.task_management.first_backend.application.enums.ProjectUserStatus;
import com.task_management.first_backend.application.helpers.DateHelper;
import com.task_management.first_backend.application.models.ProjectUser;
import com.task_management.first_backend.application.models.User;
import lombok.Data;

@Data
public class ProjectMemberDTO {
    private Long id;
    private String name;
    private String email;
    private Long userId;
    private String role;
    private ProjectUserStatus status;
    private String requestedAt;
    private String acceptedAt;

    public ProjectMemberDTO(ProjectUser projectUser){
        setId(projectUser.getId());
        setUserId(projectUser.getAssignedTo().getId());
        setName(projectUser.getAssignedTo().getFullName());
        String userEmail = projectUser.getAssignedTo().getEmail();
        setEmail( userEmail.substring(0, 3) + "*****" + userEmail.substring(6));
        setStatus(projectUser.getStatus());
        setRole(projectUser.getRole());
        setRequestedAt(DateHelper.formatDateToMD(projectUser.getCreatedAt()));
        if(projectUser.getAcceptedAt() != null){
            setAcceptedAt(DateHelper.formatDateToMD(projectUser.getAcceptedAt()));
        }
    }
}
