package com.task_management.first_backend.application.dto.projects;

import com.task_management.first_backend.application.enums.ProjectStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class ProjectRequestDTO {
    @NotBlank(message = "Project Name Is Required")
    private String name;
    @NotBlank(message = "Project Description is required")
    private String description;
    @NotBlank(message = "Project Status Is Required")
    private String status;
    @NotNull(message = "Due Date field is required")
    @Future
    private Date dueDate;
}
