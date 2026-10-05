package com.task_management.first_backend.application.tasks.services;

import com.task_management.first_backend.application.notifications.enums.NotificationType;
import com.task_management.first_backend.application.notifications.services.NotificationService;
import com.task_management.first_backend.application.projects.models.Project;
import com.task_management.first_backend.application.projects.repositories.ProjectRepository;
import com.task_management.first_backend.application.shared.helpers.DateHelper;
import com.task_management.first_backend.application.tasks.dto.tasks.TaskCreateRequestDTO;
import com.task_management.first_backend.application.tasks.dto.tasks.TaskDTO;
import com.task_management.first_backend.application.tasks.dto.tasks.TaskUpdateRequestDTO;
import com.task_management.first_backend.application.tasks.dto.tasks.TaskUpdateStatusRequestDTO;
import com.task_management.first_backend.application.tasks.enums.TaskPriority;
import com.task_management.first_backend.application.tasks.enums.TaskStatus;
import com.task_management.first_backend.application.tasks.models.Task;
import com.task_management.first_backend.application.tasks.repositories.TaskRepository;
import com.task_management.first_backend.application.users.models.User;
import com.task_management.first_backend.application.users.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.task_management.first_backend.application.notifications.services.NotificationService;
@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {
    private final TaskRepository repository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    private final NotificationService notificationService;
    public Page<TaskDTO> getAllUsersTask(User user, int page, int limit){
        Pageable pageable = PageRequest.of(page, limit);
        return repository.findByCreatedByIdOrAssignedToId(user.getId(), user.getId(), pageable)
                .map(this::mapToDTO);
    }
    private TaskDTO mapToDTO(Task task){
        return new TaskDTO(
                task
        );
    }

    public TaskDTO createTask(TaskCreateRequestDTO request, User user) {

        Long assignedToId = request.getAssignedToId() != null
                ? request.getAssignedToId()
                : user.getId();

        User assignedTo = userRepository.findById(assignedToId)
                .orElseThrow(() -> new EntityNotFoundException("Assigned user not found"));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null
                        ? request.getPriority()
                        : TaskPriority.MEDIUM)
                .status(request.getStatus() != null
                        ? request.getStatus()
                        : TaskStatus.TODO)
                .project(project)
                .assignedTo(assignedTo)
                .createdBy(user)
                .dueDate(request.getDueDate() != null
                        ? DateHelper.dateToLocaleDateTime(request.getDueDate())
                        : null)
                .build();

        Task savedTask = repository.save(task);
        notificationService.createNotification(
                assignedTo,
                "Task Assignment - " + savedTask.getTitle(),
                "A new task has been assigned to you, check the tasks page for more details",
                NotificationType.TASK_ASSIGNMENT,
                user
        );

        return new TaskDTO(savedTask);
    }

    @Transactional
    public TaskDTO updateTask(Long taskId, TaskUpdateRequestDTO request, User currentUser) {

        Task task = repository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        // Optional: Authorization check (recommended)
//        if (!task.getCreatedBy().getId().equals(currentUser.getId())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this task");
//        }

        // Update title
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }

        // Update description
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        // Update priority
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        // Update status
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        // Update due date
        if (request.getDueDate() != null) {
            task.setDueDate(DateHelper.dateToLocaleDateTime(request.getDueDate()));
        }

        // Update assigned user (only change assigned if you are creator)
        if (request.getAssignedToId() != null && task.getCreatedBy().getId().equals(currentUser.getId())) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new EntityNotFoundException("Assigned user not found"));
            if(!Objects.equals(request.getAssignedToId(), task.getAssignedTo().getId())){
                notificationService.createNotification(
                        assignedTo,
                        "Task Assignment - " + task.getTitle(),
                        "A new task has been assigned to you, check the tasks page for more details",
                        NotificationType.TASK_ASSIGNMENT,
                        currentUser
                );
            }
            task.setAssignedTo(assignedTo);

        }

        // Update project
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Project not found"));
            task.setProject(project);
        }

        //update progress
        if (request.getProgress() >= 0){
            task.setProgress(request.getProgress());
        }

        Task updatedTask = repository.save(task);

        return new TaskDTO(updatedTask);
    }

    @Transactional
    public TaskDTO updateTaskStatus(Long taskId, TaskUpdateStatusRequestDTO request, User currentUser) {

        Task task = repository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        // Optional: Authorization check (recommended)
        if (!task.getAssignedTo().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this task");
        }

        // Update status
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        //update progress
        if (request.getProgress() >= 0){
            task.setProgress(request.getProgress());
        }

        Task updatedTask = repository.save(task);

        return new TaskDTO(updatedTask);
    }

    public long getExpiringTasksCount(LocalDateTime startDate, LocalDateTime endDate){
        return repository.getTasksCountWithinDateRange(startDate, endDate);
    }
    public Page<Task> getExpiringTasks(
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return repository.getTasksWithinDateRange(startDate, endDate, pageable);

    }

    public Page<Task> getOverdueTasks(LocalDateTime end, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return repository.getTasksAfterDate(end, pageable);
    }

}
