package com.task_management.first_backend.application.projects.dto.projects;

import lombok.Data;

@Data
public class ProjectMemberInviteRequestDTO {
    private Long invitedUserId;
    private String role;
}
