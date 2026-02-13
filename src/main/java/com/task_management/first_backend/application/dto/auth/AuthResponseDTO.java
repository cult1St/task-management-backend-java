package com.task_management.first_backend.application.dto.auth;

import com.task_management.first_backend.application.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthResponseDTO {
    private UserResponseDTO user;
    private String token;
}
