package com.task_management.first_backend.application.dto.projects;

import lombok.Data;

@Data
public class ProjectMemberInviteRequestDTO {
    private Long invitedUserId;
    private String role;
}
