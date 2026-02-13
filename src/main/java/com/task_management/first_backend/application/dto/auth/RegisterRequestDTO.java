package com.task_management.first_backend.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank(message = "Fullname is Required")
    @Size(min = 3, message = "Fullname field must contain at least 3 characters")
    private String fullName;

    @NotBlank(message = "Email is Required")
    @Email(message = "Email must be a valid email")
    private String email;

    @NotBlank(message = "Password field is Required")
    @Size(min = 6, message = "Password must contain at least 6 characters")
    private String password;
}
