package com.task_management.first_backend.application.models;

import com.task_management.first_backend.application.enums.TaskPriority;
import com.task_management.first_backend.application.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tasks")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(name = "content")
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority = TaskPriority.MEDIUM;
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;
    private Project project;
}
