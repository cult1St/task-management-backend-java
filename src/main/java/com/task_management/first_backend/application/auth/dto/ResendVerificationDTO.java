package com.task_management.first_backend.application.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResendVerificationDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email")
    private String email;
}
