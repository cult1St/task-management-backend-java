package com.task_management.first_backend.application.dto.tasks;

import com.task_management.first_backend.application.enums.TaskStatus;
import lombok.Data;

@Data
public class TaskUpdateStatusRequestDTO {
    private TaskStatus status;
    private int progress;
}
