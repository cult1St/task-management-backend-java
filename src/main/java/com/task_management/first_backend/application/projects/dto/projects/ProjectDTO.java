package com.task_management.first_backend.application.projects.dto.projects;

import com.task_management.first_backend.application.projects.enums.ProjectStatus;
import com.task_management.first_backend.application.projects.enums.ProjectType;
import com.task_management.first_backend.application.shared.helpers.DateHelper;
import com.task_management.first_backend.application.projects.models.Project;
import lombok.Data;

@Data
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private String key;
    private ProjectType projectType;
    private Long workspaceId;
    private ProjectStatus status;
    private int progress;
    private String dueDate;

    public ProjectDTO(Project project) {
        setId(project.getId());
        setName(project.getName());
        setDescription(project.getDescription());
        setKey(project.getKey());
        setProjectType(project.getProjectType());
        if (project.getWorkspace() != null) {
            setWorkspaceId(project.getWorkspace().getId());
        }
        setStatus(project.getStatus());
        setProgress(project.getProgress());
        if (project.getDueDate() != null) {
            setDueDate(DateHelper.formatDateToMD(project.getDueDate()));
        }
    }
}
