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
    private Date lastLoginAt;

    public UserResponseDTO(User user){
        id = user.getId();
        fullName = user.getFullName();
        email = user.getEmail();
        lastLoginAt = user.getLastLoginAt();
    }
}
