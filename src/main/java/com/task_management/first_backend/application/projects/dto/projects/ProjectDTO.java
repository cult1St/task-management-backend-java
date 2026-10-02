package com.task_management.first_backend.application.dto.projects;

import com.task_management.first_backend.application.enums.ProjectStatus;
import com.task_management.first_backend.application.helpers.DateHelper;
import com.task_management.first_backend.application.models.Project;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private int progress;
    private String dueDate;

    public ProjectDTO(Project project){
        setId(project.getId());
        setName(project.getName());
        setDescription(project.getDescription());
        setStatus(project.getStatus());
        setProgress(project.getProgress());
        String dueDate = DateHelper.formatDateToMD(project.getDueDate());
        setDueDate(dueDate);
    }
}

