package com.task_management.first_backend.application.dto.tasks;

import com.task_management.first_backend.application.enums.TaskPriority;
import com.task_management.first_backend.application.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class TaskUpdateRequestDTO {

    private String title;

    private String description;

    private TaskPriority priority; // optional, default handled in service
    private TaskStatus status;// optional, default handled in service
    private int progress;

    private Long projectId;

    private Long assignedToId; // optional

    private Date dueDate; // optional depending on business rules
}
