package com.task_management.first_backend.application.services;

import com.task_management.first_backend.application.dto.projects.ProjectDTO;
import com.task_management.first_backend.application.dto.projects.ProjectMemberDTO;
import com.task_management.first_backend.application.dto.projects.ProjectRequestDTO;
import com.task_management.first_backend.application.enums.ProjectStatus;
import com.task_management.first_backend.application.enums.ProjectUserStatus;
import com.task_management.first_backend.application.models.Project;
import com.task_management.first_backend.application.models.ProjectUser;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.repositories.ProjectRepository;
import com.task_management.first_backend.application.repositories.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;

    public Page<ProjectDTO> getUserProjects(Long userId, String status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        // If status is null or "all", return all
        if (status == null || status.equalsIgnoreCase("all")) {
            return projectRepository
                    .findByUserId(userId, pageable)
                    .map(this::mapToDTO);
        }

        // Try to match enum safely
        ProjectStatus projectStatus = null;

        for (ProjectStatus ps : ProjectStatus.values()) {
            if (ps.name().equalsIgnoreCase(status)) {
                projectStatus = ps;
                break;
            }
        }

        // If not found in enum → fallback to ALL
        if (projectStatus == null) {
            return projectRepository
                    .findByUserId(userId, pageable)
                    .map(this::mapToDTO);
        }

        // If valid enum → filter by status
        return projectRepository
                .findByUserIdAndStatus(userId, projectStatus, pageable)
                .map(this::mapToDTO);
    }

    private ProjectDTO mapToDTO(Project project) {
        return new ProjectDTO(
                project
        );
    }

    public ProjectDTO createUserProject(User user, ProjectRequestDTO request) {
        //create project
        ProjectStatus projectStatus;
        try {
            projectStatus = ProjectStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid project status: " + request.getStatus());
        }
        Project project = Project.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .status(projectStatus)
                .dueDate(request.getDueDate())
                .build();

        projectRepository.save(project);
        return new ProjectDTO(project);
    }

    public ProjectDTO getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found with id: " + projectId
                ));

        return new ProjectDTO(project);
    }

    public Page<ProjectMemberDTO> getUserProjectMembers(User user,Long projectId, String status, int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        // If status is null or "all", return all
        if (status == null || status.equalsIgnoreCase("all")) {
            return projectUserRepository
                    .findByProjectIdAndAssignedBy(projectId, user.getId(), pageable)
                    .map(this::mapMemberToDto);
        }

        // Try to match enum safely
        ProjectUserStatus projectUserStatus = null;

        for (ProjectUserStatus ps : ProjectUserStatus.values()) {
            if (ps.name().equalsIgnoreCase(status)) {
                projectUserStatus = ps;
                break;
            }
        }

        // If not found in enum → fallback to ALL
        if (projectUserStatus == null) {
            return projectUserRepository
                    .findByProjectIdAndAssignedBy(projectId, user.getId(), pageable)
                    .map(this::mapMemberToDto);
        }

        return projectUserRepository
                .findByProjectIdAndStatusAndAssignedBy(projectId, projectUserStatus, user.getId(), pageable)
                .map(this::mapMemberToDto);

    }

    private ProjectMemberDTO mapMemberToDto(ProjectUser projectUser){
        return new ProjectMemberDTO(
                projectUser
        );
    }


}
