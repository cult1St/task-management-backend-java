package com.task_management.first_backend.application.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.task_management.first_backend.application.dto.SuccessResponse;
import com.task_management.first_backend.application.dto.projects.ProjectDTO;
import com.task_management.first_backend.application.dto.projects.ProjectMemberDTO;
import com.task_management.first_backend.application.dto.projects.ProjectRequestDTO;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<SuccessResponse<Page<ProjectDTO>>> getUserProjects(
            @AuthenticationPrincipal User authUser,
            @RequestParam(name = "status", defaultValue = "All") String status,
            @RequestParam(name = "limit", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        //set correctly params
        size = size > 0 ? size : 20;
        page = page > 0 ? (page - 1) : 0;
        Page<ProjectDTO> projectpage = projectService
                .getUserProjects(authUser.getId(), status, page, size);
        return ResponseEntity.ok(
                SuccessResponse.of("User Projects Fetched Successfully", projectpage)
        );
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<ProjectDTO>> createProject(
            @Valid @RequestBody ProjectRequestDTO request,
            @AuthenticationPrincipal User authUser
            ){
        ProjectDTO response = projectService.createUserProject(authUser, request);
        return ResponseEntity.ok(
                SuccessResponse.of("Project Created Successfully", response)
        );
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<SuccessResponse<ProjectDTO>> getProject(
            @PathVariable Long projectId
    ){
        ProjectDTO response = projectService.getProjectById(projectId);
        return ResponseEntity.ok(
                SuccessResponse.of("Project Details fetched successfully", response)
        );
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<SuccessResponse<Page<ProjectMemberDTO>>> getProjectMembers(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User authUser,
            @RequestParam(name = "status", defaultValue = "All") String status,
            @RequestParam(name = "limit", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        //set correctly params
        size = size > 0 ? size : 20;
        page = page > 0 ? (page - 1) : 0;
        Page<ProjectMemberDTO> response = projectService.getUserProjectMembers(authUser, projectId, status, page, size);
        return ResponseEntity.ok(
                SuccessResponse.of("Project Members fetched successfully", response)
        );
    }
}
