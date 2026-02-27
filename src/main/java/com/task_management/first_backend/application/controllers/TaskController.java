package com.task_management.first_backend.application.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.task_management.first_backend.application.dto.SuccessResponse;
import com.task_management.first_backend.application.dto.tasks.TaskCreateRequestDTO;
import com.task_management.first_backend.application.dto.tasks.TaskDTO;
import com.task_management.first_backend.application.dto.tasks.TaskUpdateRequestDTO;
import com.task_management.first_backend.application.dto.tasks.TaskUpdateStatusRequestDTO;
import com.task_management.first_backend.application.models.Task;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<SuccessResponse<Page<TaskDTO>>> getUsersTasks(
            @AuthenticationPrincipal User authUser,
            @RequestParam(name = "status", defaultValue = "All") String status,
            @RequestParam(name = "limit", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
            ){
        //set correctly params
        size = size > 0 ? size : 20;
        page = page > 0 ? (page - 1) : 0;
        Page<TaskDTO> tasks = taskService.getAllUsersTask(authUser, page, size);
        return ResponseEntity.ok(
                SuccessResponse.of("Users tasks fetched successfully", tasks)
        );
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<TaskDTO>> createTask(
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody TaskCreateRequestDTO requestDTO
            ){
        TaskDTO createdTask = taskService.createTask(requestDTO, authUser);
        return ResponseEntity.ok(
                SuccessResponse.of("Task Created successfully", createdTask)
        );
    }
    @PatchMapping("/{taskId}")
    public ResponseEntity<SuccessResponse<TaskDTO>> updateTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody TaskUpdateRequestDTO requestDTO
    ){
        TaskDTO updatedTask = taskService.updateTask(taskId, requestDTO, authUser);
        return ResponseEntity.ok(
                SuccessResponse.of("Task Updated successfully", updatedTask)
        );
    }

    @PatchMapping("/{taskId}/update-status")
    public ResponseEntity<SuccessResponse<TaskDTO>> updateTaskStatus(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody TaskUpdateStatusRequestDTO requestDTO
    ){
        TaskDTO updatedTask = taskService.updateTaskStatus(taskId, requestDTO, authUser);
        return ResponseEntity.ok(
                SuccessResponse.of("Task Updated successfully", updatedTask)
        );
    }
}
