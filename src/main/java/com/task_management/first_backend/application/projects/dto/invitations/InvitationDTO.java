package com.task_management.first_backend.application.projects.dto.invitations;

import com.task_management.first_backend.application.projects.models.ProjectUser;
import com.task_management.first_backend.application.team.models.WorkspaceInvite;
import com.task_management.first_backend.application.users.models.User;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InvitationDTO {
    private Long id;
    private Long workspaceId;
    private String workspaceName;
    private Long projectId;
    private String projectName;
    private Long inviterId;
    private String inviterName;
    private Long invitedUserId;
    private String invitedUserName;
    private String invitedUserEmail;
    private String role;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public InvitationDTO(ProjectUser projectUser) {
        setId(projectUser.getId());
        if (projectUser.getProject() != null) {
            setProjectId(projectUser.getProject().getId());
            setProjectName(projectUser.getProject().getName());
            if (projectUser.getProject().getWorkspace() != null) {
                setWorkspaceId(projectUser.getProject().getWorkspace().getId());
                setWorkspaceName(projectUser.getProject().getWorkspace().getName());
            }
        }
        if (projectUser.getAssignedBy() != null) {
            setInviterId(projectUser.getAssignedBy().getId());
            setInviterName(projectUser.getAssignedBy().getFullName());
        }
        if (projectUser.getAssignedTo() != null) {
            setInvitedUserId(projectUser.getAssignedTo().getId());
            setInvitedUserName(projectUser.getAssignedTo().getFullName());
            setInvitedUserEmail(maskEmail(projectUser.getAssignedTo().getEmail()));
        }
        setRole(projectUser.getRole());
        setStatus(projectUser.getStatus() != null ? projectUser.getStatus().name() : null);
        setCreatedAt(toLocalDate(projectUser.getCreatedAt()));
        setUpdatedAt(toLocalDate(projectUser.getUpdatedAt()));
    }

    public static InvitationDTO fromWorkspaceInvite(WorkspaceInvite invite, User invitedUser) {
        InvitationDTO dto = new InvitationDTO();
        dto.setId(invite.getId());
        if (invite.getWorkspace() != null) {
            dto.setWorkspaceId(invite.getWorkspace().getId());
            dto.setWorkspaceName(invite.getWorkspace().getName());
        }
        dto.setProjectId(null);
        dto.setProjectName(null);
        if (invite.getInvitedBy() != null) {
            dto.setInviterId(invite.getInvitedBy().getId());
            dto.setInviterName(invite.getInvitedBy().getFullName());
        }
        if (invitedUser != null) {
            dto.setInvitedUserId(invitedUser.getId());
            dto.setInvitedUserName(invitedUser.getFullName());
        }
        dto.setInvitedUserEmail(maskEmail(invite.getEmail()));
        dto.setRole(invite.getRole());
        dto.setStatus(invite.getStatus() != null ? invite.getStatus().name() : null);
        dto.setCreatedAt(toLocalDate(invite.getCreatedAt()));
        dto.setUpdatedAt(toLocalDate(invite.getUpdatedAt()));
        return dto;
    }

    private static String maskEmail(String email) {
        if (email == null || email.length() < 5) {
            return "*****";
        }
        return email.substring(0, Math.min(3, email.length())) + "*****"
                + (email.length() > 6 ? email.substring(6) : "");
    }

    private static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
