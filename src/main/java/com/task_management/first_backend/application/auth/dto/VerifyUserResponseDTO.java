package com.task_management.first_backend.application.auth.dto;

import com.task_management.first_backend.application.users.dto.UserResponseDTO;
import com.task_management.first_backend.application.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import com.task_management.first_backend.application.users.models.User;
@Data
@AllArgsConstructor
public class VerifyUserResponseDTO {
    private String token;
    private UserResponseDTO user;
    private OnboardingDTO onboarding;
}
