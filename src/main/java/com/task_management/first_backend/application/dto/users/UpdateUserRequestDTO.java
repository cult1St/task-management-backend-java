package com.task_management.first_backend.application.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserRequestDTO {
    private String fullName;
    private String email;
    private String roleTitle;
    private String avatarUrl;
}
