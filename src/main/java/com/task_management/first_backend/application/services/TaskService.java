package com.task_management.first_backend.application.services;

import com.task_management.first_backend.application.dto.tasks.TaskCreateRequestDTO;
import com.task_management.first_backend.application.dto.tasks.TaskDTO;
import com.task_management.first_backend.application.dto.tasks.TaskUpdateRequestDTO;
import com.task_management.first_backend.application.dto.tasks.TaskUpdateStatusRequestDTO;
import com.task_management.first_backend.application.enums.TaskPriority;
import com.task_management.first_backend.application.enums.TaskStatus;
import com.task_management.first_backend.application.models.Project;
import com.task_management.first_backend.application.models.Task;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.repositories.ProjectRepository;
import com.task_management.first_backend.application.repositories.TaskRepository;
import com.task_management.first_backend.application.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
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

    @Transactional
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
                        ? request.getDueDate()
                        : null)
                .build();

        Task savedTask = repository.save(task);

        return new TaskDTO(savedTask);
    }

    @Transactional
    public TaskDTO updateTask(Long taskId, TaskUpdateRequestDTO request, User currentUser) {

        Task task = repository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        // Optional: Authorization check (recommended)
        if (!task.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this task");
        }

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
            task.setDueDate(request.getDueDate());
        }

        // Update assigned user
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new EntityNotFoundException("Assigned user not found"));
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

}
