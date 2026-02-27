package com.task_management.first_backend.application.dto.invitations;

import com.task_management.first_backend.application.enums.InvitationResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvitationRespondRequestDTO {
    @NotNull
    private InvitationResponse action;
}
