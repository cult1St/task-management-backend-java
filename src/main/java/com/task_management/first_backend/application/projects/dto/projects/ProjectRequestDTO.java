package com.task_management.first_backend.application.projects.dto.projects;

import com.task_management.first_backend.application.projects.enums.ProjectType;
import com.task_management.first_backend.application.projects.models.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Date;
import lombok.Data;

import com.task_management.first_backend.application.projects.models.Project;
@Data
public class ProjectRequestDTO {
    @NotBlank(message = "Project Name Is Required")
    private String name;

    private String description;

    @NotBlank(message = "Project key is required")
    @Size(min = 2, max = 10, message = "Project key must be 2-10 characters")
    @Pattern(regexp = "^[A-Z][A-Z0-9]{1,9}$", message = "Project key must match ^[A-Z][A-Z0-9]{1,9}$")
    private String key;

    private ProjectType projectType = ProjectType.SOFTWARE;

    private String status = "ACTIVE";

    private Date dueDate;
}
