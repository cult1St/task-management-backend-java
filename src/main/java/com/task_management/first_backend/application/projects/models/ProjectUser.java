package com.task_management.first_backend.application.models;

import com.task_management.first_backend.application.enums.ProjectUserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Builder
@Table(
        name = "project_users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "assigned_to"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "assigned_by")
    private User assignedBy;
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    @Enumerated(EnumType.STRING)
    private ProjectUserStatus status = ProjectUserStatus.PENDING;
    private String role;
    private Date acceptedAt;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
}
