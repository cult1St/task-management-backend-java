package com.task_management.first_backend.application.auth.dto;

import com.task_management.first_backend.application.users.dto.UserResponseDTO;
import com.task_management.first_backend.application.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.task_management.first_backend.application.users.models.User;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponseDTO {
    private UserResponseDTO user;
    private String token;
    private OnboardingDTO onboarding;

    public AuthResponseDTO(UserResponseDTO user, String token) {
        this.user = user;
        this.token = token;
        this.onboarding = user != null ? user.getOnboarding() : null;
    }
}
