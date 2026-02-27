package com.task_management.first_backend.application.services;

import com.task_management.first_backend.application.dto.invitations.InvitationDTO;
import com.task_management.first_backend.application.dto.invitations.InvitationRespondRequestDTO;
import com.task_management.first_backend.application.dto.projects.ProjectDTO;
import com.task_management.first_backend.application.dto.projects.ProjectMemberDTO;
import com.task_management.first_backend.application.dto.projects.ProjectMemberInviteRequestDTO;
import com.task_management.first_backend.application.dto.projects.ProjectRequestDTO;
import com.task_management.first_backend.application.enums.InvitationResponse;
import com.task_management.first_backend.application.enums.ProjectStatus;
import com.task_management.first_backend.application.enums.ProjectUserStatus;
import com.task_management.first_backend.application.models.Project;
import com.task_management.first_backend.application.models.ProjectUser;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.repositories.ProjectRepository;
import com.task_management.first_backend.application.repositories.ProjectUserRepository;
import com.task_management.first_backend.application.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;

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
                    .findByProjectIdAndAssignedBy(projectId, user, pageable)
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
                    .findByProjectIdAndAssignedBy(projectId, user, pageable)
                    .map(this::mapMemberToDto);
        }

        return projectUserRepository
                .findByProjectIdAndStatusAndAssignedBy(projectId, projectUserStatus, user, pageable)
                .map(this::mapMemberToDto);

    }

    public ProjectMemberDTO inviteMember(Long projectId, ProjectMemberInviteRequestDTO requestDTO, User user){
        //check if project exist
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Product Not Found"));
        //check if assign to user exists
        User assignee = userRepository.findById(requestDTO.getInvitedUserId())
                .orElseThrow(() -> new EntityNotFoundException("Assignee User does not exist"));
        //check for redundant requests
        Pageable pageable = PageRequest.of(0, 50);
        Page<ProjectUser> prevRequest = projectUserRepository
                .findByProjectIdAndAssignedBy(projectId, assignee, pageable);

        if(prevRequest.stream().findAny().isPresent()){
            throw new BadCredentialsException(
                    "A Request already exists for this User, please wait for confirmation"
            );
        }
        //check if user owns the project
        if(!Objects.equals(project.getUser().getId(), user.getId())){
            throw new BadCredentialsException(
                    "You are not allowed to send a request. please contact project owner"
            );
        }


        //create the request
        ProjectUser newRequest = ProjectUser.builder()
                .project(project)
                .role(requestDTO.getRole())
                .status(ProjectUserStatus.PENDING)
                .assignedBy(user)
                .assignedTo(assignee)
                .build();
        ProjectUser createdRequest = projectUserRepository.save(newRequest);
        return new ProjectMemberDTO(createdRequest);
    }


    public Page<InvitationDTO> getReceivedInvitations(User user, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return projectUserRepository
                .findByAssignedTo(user, pageable)
                .map(this::mapMemberToInvitationDto);
    }
    public Page<InvitationDTO> getSentInvitations(User user, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return projectUserRepository
                .findByAssignedBy(user, pageable)
                .map(this::mapMemberToInvitationDto);
    }

    public InvitationDTO respondToInvitationRequest(Long invitationId, User user, InvitationRespondRequestDTO requestDTO){
        //check if invitation exist
        ProjectUser projectUser = projectUserRepository
                .findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation Request Not found"));
        if (requestDTO.getAction() == InvitationResponse.ACCEPT){
            projectUser.setStatus(ProjectUserStatus.ACCEPTED);
        }else{
            projectUser.setStatus(ProjectUserStatus.REJECTED);
        }
        projectUserRepository.save(projectUser);
        return new InvitationDTO(projectUser);

    }

    private ProjectMemberDTO mapMemberToDto(ProjectUser projectUser){
        return new ProjectMemberDTO(
                projectUser
        );
    }
    private InvitationDTO mapMemberToInvitationDto(ProjectUser projectUser){
        return new InvitationDTO(
                projectUser
        );
    }




}
