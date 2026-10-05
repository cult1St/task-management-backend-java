package com.task_management.first_backend.application.tasks.dto.tasks;

import com.task_management.first_backend.application.tasks.enums.TaskStatus;
import lombok.Data;

@Data
public class TaskUpdateStatusRequestDTO {
    private TaskStatus status;
    private int progress;
}
