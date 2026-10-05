package com.task_management.first_backend.application.users.dto;

import com.task_management.first_backend.application.auth.dto.OnboardingDTO;
import com.task_management.first_backend.application.auth.helpers.OnboardingMapper;
import com.task_management.first_backend.application.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String avatarUrl;
    private Date lastLoginAt;
    private OnboardingDTO onboarding;

    public UserResponseDTO(User user) {
        id = user.getId();
        fullName = user.getFullName();
        email = user.getEmail();
        role = user.getDesignatedRole() != null ? user.getDesignatedRole() : "Member";
        avatarUrl = user.getAvatarUrl();
        lastLoginAt = user.getLastLoginAt();
        onboarding = OnboardingMapper.from(user.getOnboarding());
    }
}
