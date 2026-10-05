package com.task_management.first_backend.application.projects.models;

import com.task_management.first_backend.application.projects.enums.ProjectStatus;
import com.task_management.first_backend.application.projects.enums.ProjectType;
import com.task_management.first_backend.application.tasks.models.Task;
import com.task_management.first_backend.application.users.models.User;
import com.task_management.first_backend.application.workspace.models.Workspace;

import com.task_management.first_backend.application.tasks.models.Task;
import com.task_management.first_backend.application.users.models.User;
import com.task_management.first_backend.application.workspace.models.Workspace;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@Table(
        name = "projects",
        uniqueConstraints = @UniqueConstraint(columnNames = {"workspace_id", "project_key"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "content")
    private String description;

    @Column(name = "project_key", length = 10)
    private String key;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectType projectType = ProjectType.SOFTWARE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @Column(nullable = false)
    private Date dueDate;

    @Builder.Default
    private int progress = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectUser> projectUsers = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
