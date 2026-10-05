package com.task_management.first_backend.application.projects.dto.invitations;

import com.task_management.first_backend.application.projects.enums.InvitationResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvitationRespondRequestDTO {
    @NotNull
    private InvitationResponse action;
}
