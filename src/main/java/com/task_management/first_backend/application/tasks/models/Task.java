package com.task_management.first_backend.application.tasks.models;

import com.task_management.first_backend.application.projects.models.Project;
import com.task_management.first_backend.application.tasks.enums.TaskPriority;
import com.task_management.first_backend.application.tasks.enums.TaskStatus;
import com.task_management.first_backend.application.users.models.User;

import com.task_management.first_backend.application.projects.models.Project;
import com.task_management.first_backend.application.users.models.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

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
    private int progress = 0;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    @Column(nullable = false)
    private LocalDateTime dueDate;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
}
