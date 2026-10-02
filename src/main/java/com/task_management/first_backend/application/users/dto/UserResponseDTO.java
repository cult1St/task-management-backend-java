package com.task_management.first_backend.application.dto;

import com.task_management.first_backend.application.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String avatarUrl;
    private Date lastLoginAt;

    public UserResponseDTO(User user){
        id = user.getId();
        fullName = user.getFullName();
        email = user.getEmail();
        role = user.getDesignatedRole();
        avatarUrl = user.getAvatarUrl();
        lastLoginAt = user.getLastLoginAt();
    }
}
