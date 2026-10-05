package com.task_management.first_backend.application.projects.services;

import com.task_management.first_backend.application.activity.enums.WorkspaceActivityType;
import com.task_management.first_backend.application.activity.services.WorkspaceActivityService;
import com.task_management.first_backend.application.notifications.enums.NotificationType;
import com.task_management.first_backend.application.notifications.services.NotificationService;
import com.task_management.first_backend.application.projects.dto.invitations.InvitationDTO;
import com.task_management.first_backend.application.projects.dto.invitations.InvitationRespondRequestDTO;
import com.task_management.first_backend.application.projects.dto.projects.ProjectDTO;
import com.task_management.first_backend.application.projects.dto.projects.ProjectMemberDTO;
import com.task_management.first_backend.application.projects.dto.projects.ProjectMemberInviteRequestDTO;
import com.task_management.first_backend.application.projects.dto.projects.ProjectRequestDTO;
import com.task_management.first_backend.application.projects.enums.InvitationResponse;
import com.task_management.first_backend.application.projects.enums.ProjectStatus;
import com.task_management.first_backend.application.projects.enums.ProjectType;
import com.task_management.first_backend.application.projects.enums.ProjectUserStatus;
import com.task_management.first_backend.application.projects.models.Project;
import com.task_management.first_backend.application.projects.models.ProjectUser;
import com.task_management.first_backend.application.projects.repositories.ProjectRepository;
import com.task_management.first_backend.application.projects.repositories.ProjectUserRepository;
import com.task_management.first_backend.application.team.enums.WorkspaceInviteStatus;
import com.task_management.first_backend.application.team.models.WorkspaceInvite;
import com.task_management.first_backend.application.team.repositories.WorkspaceInviteRepository;
import com.task_management.first_backend.application.team.repositories.WorkspaceMemberRepository;
import com.task_management.first_backend.application.team.services.WorkspaceTeamService;
import com.task_management.first_backend.application.users.models.User;
import com.task_management.first_backend.application.users.repositories.UserRepository;
import com.task_management.first_backend.application.workspace.models.Workspace;
import com.task_management.first_backend.application.workspace.services.WorkspaceAccessService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;
    private final WorkspaceAccessService workspaceAccessService;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceInviteRepository workspaceInviteRepository;
    private final WorkspaceTeamService workspaceTeamService;
    private final NotificationService notificationService;
    private final WorkspaceActivityService workspaceActivityService;

    public Page<ProjectDTO> getUserProjects(User user, String status, int page, int size) {
        Workspace workspace = workspaceAccessService.requireCurrentWorkspace(user);
        Pageable pageable = PageRequest.of(page, size);

        if (status == null || status.equalsIgnoreCase("all")) {
            return projectRepository
                    .findByWorkspaceId(workspace.getId(), pageable)
                    .map(this::mapToDTO);
        }

        ProjectStatus projectStatus = parseStatusOrNull(status);
        if (projectStatus == null) {
            return projectRepository
                    .findByWorkspaceId(workspace.getId(), pageable)
                    .map(this::mapToDTO);
        }

        return projectRepository
                .findByWorkspaceIdAndStatus(workspace.getId(), projectStatus, pageable)
                .map(this::mapToDTO);
    }

    public ProjectDTO createUserProject(User user, ProjectRequestDTO request) {
        Workspace workspace = workspaceAccessService.requireCurrentWorkspace(user);
        workspaceAccessService.requireCanCreateProject(user, workspace);

        String key = normalizeKey(request.getKey());
        if (projectRepository.existsByWorkspaceIdAndKeyIgnoreCase(workspace.getId(), key)) {
            throw new IllegalArgumentException("Project key already exists in this workspace: " + key);
        }

        ProjectStatus projectStatus = parseStatusRequired(
                request.getStatus() != null ? request.getStatus() : "ACTIVE"
        );
        ProjectType projectType = request.getProjectType() != null
                ? request.getProjectType()
                : ProjectType.SOFTWARE;

        Project project = Project.builder()
                .user(user)
                .workspace(workspace)
                .name(request.getName().trim())
                .description(request.getDescription())
                .key(key)
                .projectType(projectType)
                .status(projectStatus)
                .dueDate(resolveDueDate(request.getDueDate()))
                .build();

        projectRepository.save(project);
        workspaceActivityService.record(
                workspace,
                user,
                WorkspaceActivityType.PROJECT_CREATED,
                user.getFullName() + " created project " + project.getKey(),
                "PROJECT",
                project.getId(),
                project.getId()
        );
        return new ProjectDTO(project);
    }

    public ProjectDTO getProjectById(User user, Long projectId) {
        Project project = loadAccessibleProject(user, projectId);
        return new ProjectDTO(project);
    }

    public ProjectDTO updateProject(User user, Long projectId, ProjectRequestDTO request) {
        Project project = loadAccessibleProject(user, projectId);
        workspaceAccessService.requireCanManageProject(user, project.getWorkspace());

        if (request.getName() != null && !request.getName().isBlank()) {
            project.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            project.setStatus(parseStatusRequired(request.getStatus()));
        }
        if (request.getProjectType() != null) {
            project.setProjectType(request.getProjectType());
        }
        if (request.getDueDate() != null) {
            project.setDueDate(request.getDueDate());
        }
        if (request.getKey() != null && !request.getKey().isBlank()) {
            String key = normalizeKey(request.getKey());
            if (!key.equalsIgnoreCase(project.getKey())
                    && projectRepository.existsByWorkspaceIdAndKeyIgnoreCase(project.getWorkspace().getId(), key)) {
                throw new IllegalArgumentException("Project key already exists in this workspace: " + key);
            }
            project.setKey(key);
        }

        projectRepository.save(project);
        return new ProjectDTO(project);
    }

    public void deleteProject(User user, Long projectId) {
        Project project = loadAccessibleProject(user, projectId);
        workspaceAccessService.requireCanManageProject(user, project.getWorkspace());
        projectRepository.delete(project);
    }

    public Page<ProjectMemberDTO> getUserProjectMembers(User user, Long projectId, String status, int page, int size) {
        Project project = loadAccessibleProject(user, projectId);
        Pageable pageable = PageRequest.of(page, size);

        if (status == null || status.equalsIgnoreCase("all")) {
            return projectUserRepository
                    .findByProjectId(project.getId(), pageable)
                    .map(this::mapMemberToDto);
        }

        ProjectUserStatus projectUserStatus = null;
        for (ProjectUserStatus ps : ProjectUserStatus.values()) {
            if (ps.name().equalsIgnoreCase(status)) {
                projectUserStatus = ps;
                break;
            }
        }

        if (projectUserStatus == null) {
            return projectUserRepository
                    .findByProjectId(project.getId(), pageable)
                    .map(this::mapMemberToDto);
        }

        return projectUserRepository
                .findByProjectIdAndStatus(project.getId(), projectUserStatus, pageable)
                .map(this::mapMemberToDto);
    }

    public ProjectMemberDTO inviteMember(Long projectId, ProjectMemberInviteRequestDTO requestDTO, User user) {
        Project project = loadAccessibleProject(user, projectId);
        workspaceAccessService.requireCanManageProject(user, project.getWorkspace());

        User assignee = userRepository.findById(requestDTO.getInvitedUserId())
                .orElseThrow(() -> new EntityNotFoundException("Assignee User does not exist"));

        if (project.getWorkspace() != null
                && !workspaceMemberRepository.existsByWorkspaceIdAndUserId(
                        project.getWorkspace().getId(), assignee.getId())) {
            throw new IllegalArgumentException(
                    "User must be a workspace member before they can be assigned to a project"
            );
        }

        Pageable pageable = PageRequest.of(0, 50);
        Page<ProjectUser> prevRequest = projectUserRepository
                .findByProjectIdAndAssignedBy(projectId, assignee, pageable);

        if (prevRequest.stream().findAny().isPresent()) {
            throw new BadCredentialsException(
                    "A Request already exists for this User, please wait for confirmation"
            );
        }

        ProjectUser newRequest = ProjectUser.builder()
                .project(project)
                .role(requestDTO.getRole())
                .status(ProjectUserStatus.PENDING)
                .assignedBy(user)
                .assignedTo(assignee)
                .build();
        ProjectUser createdRequest = projectUserRepository.save(newRequest);

        notificationService.createNotification(
                assignee,
                "Project Invitation Request",
                "An invitation request has been sent to you on the project " + project.getName()
                        + " please check the invitations page and accept or decline invitation",
                NotificationType.INVITATION_REQUEST,
                user
        );
        return new ProjectMemberDTO(createdRequest);
    }

    public Page<InvitationDTO> getReceivedInvitations(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<InvitationDTO> workspaceInvites = workspaceInviteRepository
                .findReceivedWithWorkspace(
                        user.getEmail(),
                        EnumSet.of(WorkspaceInviteStatus.PENDING, WorkspaceInviteStatus.SENT)
                )
                .stream()
                .map(invite -> InvitationDTO.fromWorkspaceInvite(invite, user))
                .toList();

        Page<InvitationDTO> projectInvites = projectUserRepository
                .findByAssignedTo(user, pageable)
                .map(this::mapMemberToInvitationDto);

        List<InvitationDTO> combined = new ArrayList<>(workspaceInvites);
        combined.addAll(projectInvites.getContent());
        long total = workspaceInvites.size() + projectInvites.getTotalElements();
        return new PageImpl<>(combined, pageable, total);
    }

    public Page<InvitationDTO> getSentInvitations(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<InvitationDTO> workspaceInvites = workspaceInviteRepository
                .findSentWithWorkspace(user.getId())
                .stream()
                .map(invite -> {
                    User invited = userRepository.findByEmail(invite.getEmail());
                    return InvitationDTO.fromWorkspaceInvite(invite, invited);
                })
                .toList();

        Page<InvitationDTO> projectInvites = projectUserRepository
                .findByAssignedBy(user, pageable)
                .map(this::mapMemberToInvitationDto);

        List<InvitationDTO> combined = new ArrayList<>(workspaceInvites);
        combined.addAll(projectInvites.getContent());
        long total = workspaceInvites.size() + projectInvites.getTotalElements();
        return new PageImpl<>(combined, pageable, total);
    }

    public InvitationDTO respondToInvitationRequest(Long invitationId, User user, InvitationRespondRequestDTO requestDTO) {
        // Prefer project invite when the caller is the assignee (avoids rare id collisions).
        var projectInvite = projectUserRepository.findById(invitationId);
        if (projectInvite.isPresent()
                && Objects.equals(projectInvite.get().getAssignedTo().getId(), user.getId())) {
            return respondToProjectInvitation(projectInvite.get(), user, requestDTO);
        }

        WorkspaceInvite workspaceInvite = workspaceInviteRepository
                .findByIdAndEmailIgnoreCase(invitationId, user.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Invitation Request Not found"));

        if (requestDTO.getAction() == InvitationResponse.ACCEPT) {
            workspaceTeamService.acceptInvite(workspaceInvite, user);
            notificationService.createNotification(
                    workspaceInvite.getInvitedBy(),
                    "Workspace Invitation Response",
                    user.getFullName() + " accepted your workspace invitation",
                    NotificationType.INVITATION_RESPONSE,
                    user
            );
        } else {
            workspaceTeamService.rejectInvite(workspaceInvite, user);
            notificationService.createNotification(
                    workspaceInvite.getInvitedBy(),
                    "Workspace Invitation Response",
                    user.getFullName() + " declined your workspace invitation",
                    NotificationType.INVITATION_RESPONSE,
                    user
            );
        }
        return InvitationDTO.fromWorkspaceInvite(workspaceInvite, user);
    }

    private InvitationDTO respondToProjectInvitation(
            ProjectUser projectUser,
            User user,
            InvitationRespondRequestDTO requestDTO
    ) {
        String message;
        if (requestDTO.getAction() == InvitationResponse.ACCEPT) {
            projectUser.setStatus(ProjectUserStatus.ACCEPTED);
            projectUser.setAcceptedAt(new Date());
            message = "Invitation request has been accepted. you can now assign tasks on this project to this user";
        } else {
            projectUser.setStatus(ProjectUserStatus.REJECTED);
            message = "Invitation request has been rejected.";
        }
        projectUserRepository.save(projectUser);
        notificationService.createNotification(
                projectUser.getAssignedBy(),
                "Project Invitation Response",
                message,
                NotificationType.INVITATION_RESPONSE,
                user
        );
        return new InvitationDTO(projectUser);
    }

    public Boolean deleteMember(Long projectId, Long userId, User actor) {
        Project project = loadAccessibleProject(actor, projectId);
        workspaceAccessService.requireCanManageProject(actor, project.getWorkspace());

        User recipient = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));
        ProjectUser projectMember = projectUserRepository.findByProjectIdAndAssignedTo(projectId, recipient);
        if (projectMember == null) {
            throw new EntityNotFoundException("User does not belong to this project");
        }
        projectUserRepository.delete(projectMember);
        return true;
    }

    private Project loadAccessibleProject(User user, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found with id: " + projectId
                ));
        if (project.getWorkspace() == null) {
            // Legacy project without workspace: only creator can access
            if (!Objects.equals(project.getUser().getId(), user.getId())) {
                throw new AccessDeniedException("You do not have access to this workspace");
            }
            return project;
        }
        workspaceAccessService.requireMembership(user, project.getWorkspace());
        return project;
    }

    private ProjectDTO mapToDTO(Project project) {
        return new ProjectDTO(project);
    }

    private ProjectMemberDTO mapMemberToDto(ProjectUser projectUser) {
        return new ProjectMemberDTO(projectUser);
    }

    private InvitationDTO mapMemberToInvitationDto(ProjectUser projectUser) {
        return new InvitationDTO(projectUser);
    }

    private String normalizeKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Project key is required");
        }
        String normalized = key.trim().toUpperCase();
        if (!normalized.matches("^[A-Z][A-Z0-9]{1,9}$")) {
            throw new IllegalArgumentException("Project key must match ^[A-Z][A-Z0-9]{1,9}$");
        }
        return normalized;
    }

    private ProjectStatus parseStatusOrNull(String status) {
        for (ProjectStatus ps : ProjectStatus.values()) {
            if (ps.name().equalsIgnoreCase(status)) {
                return ps;
            }
        }
        return null;
    }

    private ProjectStatus parseStatusRequired(String status) {
        try {
            return ProjectStatus.valueOf(status.toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid project status: " + status);
        }
    }

    private Date resolveDueDate(Date dueDate) {
        if (dueDate != null) {
            return dueDate;
        }
        return Date.from(
                LocalDate.now().plusDays(30)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }
}
