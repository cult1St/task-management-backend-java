package com.task_management.first_backend.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Email is Required")
    @Email(message = "Email sent must be a valid email")
    private String email;
    @NotBlank(message = "Password Field is required")
    private String password;
}
