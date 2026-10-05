package com.task_management.first_backend.application.auth.dto;

import com.task_management.first_backend.application.users.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InitialRegisterResponse {
    private String email;
    private OnboardingDTO onboarding;
}
