package com.task_management.first_backend.application.dto.invitations;

import com.task_management.first_backend.application.enums.ProjectUserStatus;
import com.task_management.first_backend.application.models.ProjectUser;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZoneId;

@Data
public class InvitationDTO {
    private Long id;
    private Long projectId;
    private String projectName;
    private Long inviterId;
    private String inviterName;
    private Long invitedUserId;
    private String invitedUserName;
    private String invitedUserEmail;
    private String role;
    private ProjectUserStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public InvitationDTO(ProjectUser projectUser){
        setId(projectUser.getId());
        setProjectId(projectUser.getProject().getId());
        setProjectName(projectUser.getProject().getName());
        setInviterId(projectUser.getAssignedBy().getId());
        setInviterName(projectUser.getAssignedBy().getFullName());
        setInvitedUserId(projectUser.getAssignedTo().getId());
        setInvitedUserName(projectUser.getAssignedTo().getFullName());
        setInvitedUserEmail(projectUser.getAssignedTo().getEmail().substring(4) + "*****");
        setRole(projectUser.getRole());
        setStatus(projectUser.getStatus());
        setCreatedAt(projectUser
                .getCreatedAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        );
        setUpdatedAt(projectUser
                .getUpdatedAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        );
    }
}
