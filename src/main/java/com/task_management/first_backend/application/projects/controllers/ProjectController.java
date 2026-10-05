package com.task_management.first_backend.application.projects.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.task_management.first_backend.application.projects.dto.projects.ProjectDTO;
import com.task_management.first_backend.application.projects.dto.projects.ProjectMemberDTO;
import com.task_management.first_backend.application.projects.dto.projects.ProjectMemberInviteRequestDTO;
import com.task_management.first_backend.application.projects.dto.projects.ProjectRequestDTO;
import com.task_management.first_backend.application.projects.models.Project;
import com.task_management.first_backend.application.projects.services.ProjectService;
import com.task_management.first_backend.application.shared.dto.SuccessResponse;
import com.task_management.first_backend.application.users.models.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.task_management.first_backend.application.projects.models.Project;
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
    ) {
        size = size > 0 ? size : 20;
        page = page > 0 ? (page - 1) : 0;
        Page<ProjectDTO> projectpage = projectService
                .getUserProjects(authUser, status, page, size);
        return ResponseEntity.ok(
                SuccessResponse.of("User Projects Fetched Successfully", projectpage)
        );
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<ProjectDTO>> createProject(
            @Valid @RequestBody ProjectRequestDTO request,
            @AuthenticationPrincipal User authUser
    ) {
        ProjectDTO response = projectService.createUserProject(authUser, request);
        return ResponseEntity.ok(
                SuccessResponse.of("Project Created Successfully", response)
        );
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<SuccessResponse<ProjectDTO>> getProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User authUser
    ) {
        ProjectDTO response = projectService.getProjectById(authUser, projectId);
        return ResponseEntity.ok(
                SuccessResponse.of("Project Details fetched successfully", response)
        );
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<SuccessResponse<ProjectDTO>> updateProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User authUser,
            @RequestBody ProjectRequestDTO request
    ) {
        ProjectDTO response = projectService.updateProject(authUser, projectId, request);
        return ResponseEntity.ok(
                SuccessResponse.of("Project updated successfully", response)
        );
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<SuccessResponse<?>> deleteProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User authUser
    ) {
        projectService.deleteProject(authUser, projectId);
        return ResponseEntity.ok(
                SuccessResponse.of("Project deleted successfully")
        );
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<SuccessResponse<Page<ProjectMemberDTO>>> getProjectMembers(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User authUser,
            @RequestParam(name = "status", defaultValue = "All") String status,
            @RequestParam(name = "limit", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        size = size > 0 ? size : 20;
        page = page > 0 ? (page - 1) : 0;
        Page<ProjectMemberDTO> response = projectService.getUserProjectMembers(
                authUser, projectId, status, page, size
        );
        return ResponseEntity.ok(
                SuccessResponse.of("Project Members fetched successfully", response)
        );
    }

    @PostMapping("/{projectId}/invitations")
    public ResponseEntity<SuccessResponse<ProjectMemberDTO>> inviteMember(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody ProjectMemberInviteRequestDTO requestDTO
    ) {
        ProjectMemberDTO projectMemberRequest = projectService.inviteMember(projectId, requestDTO, authUser);
        return ResponseEntity.ok(
                SuccessResponse.of("Invitation Request Sent Successfully", projectMemberRequest)
        );
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<SuccessResponse<Boolean>> deleteMember(
            @AuthenticationPrincipal User authUser,
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        Boolean deleteMember = projectService.deleteMember(projectId, userId, authUser);
        return ResponseEntity.ok(
                SuccessResponse.of("Member deleted From Project Successfully", deleteMember)
        );
    }
}
