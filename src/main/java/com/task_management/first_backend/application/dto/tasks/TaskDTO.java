package com.task_management.first_backend.application.dto.tasks;

import com.task_management.first_backend.application.enums.TaskPriority;
import com.task_management.first_backend.application.enums.TaskStatus;
import com.task_management.first_backend.application.models.Task;
import lombok.Data;
import java.time.Instant;
import java.util.Date;

@Data
public class TaskDTO {

    private Long id;
    private String title;
    private String description;

    private TaskPriority priority;
    private TaskStatus status;
    private int progress;

    private Long projectId;
    private String projectName;

    private Long createdById;
    private String createdByName;

    private Long assignedToId;
    private String assignedToName;

    private Instant dueDate;
    private Instant createdAt;
    private Instant updatedAt;

    private boolean expired;

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();

        this.priority = task.getPriority();
        this.status = task.getStatus();
        this.progress = task.getProgress();

        // Project mapping (null-safe)
        if (task.getProject() != null) {
            this.projectId = task.getProject().getId();
            this.projectName = task.getProject().getName();
        }

        // Created By mapping (null-safe)
        if (task.getCreatedBy() != null) {
            this.createdById = task.getCreatedBy().getId();
            this.createdByName = task.getCreatedBy().getUsername(); // adjust if different
        }

        // Assigned To mapping (null-safe)
        if (task.getAssignedTo() != null) {
            this.assignedToId = task.getAssignedTo().getId();
            this.assignedToName = task.getAssignedTo().getUsername();
        }

        // Date conversion
        this.dueDate = convertToInstant(task.getDueDate());
        this.createdAt = convertToInstant(task.getCreatedAt());
        this.updatedAt = convertToInstant(task.getUpdatedAt());

        // Computed field
        this.expired = isTaskExpired(task);
    }

    private Instant convertToInstant(Date date) {
        return date != null ? date.toInstant() : null;
    }

    private boolean isTaskExpired(Task task) {
        if (task.getDueDate() == null) return false;
        if (task.getStatus() == TaskStatus.DONE) return false;
        return task.getDueDate().before(new Date());
    }
}
