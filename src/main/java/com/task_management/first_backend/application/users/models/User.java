package com.task_management.first_backend.application.users.models;

import com.task_management.first_backend.application.auth.models.OnboardingRequest;
import com.task_management.first_backend.application.notifications.models.Notification;
import com.task_management.first_backend.application.projects.models.Project;
import com.task_management.first_backend.application.projects.models.ProjectUser;
import com.task_management.first_backend.application.tasks.models.Task;
import com.task_management.first_backend.application.users.enums.UserRole;

import com.task_management.first_backend.application.notifications.models.Notification;
import com.task_management.first_backend.application.projects.models.Project;
import com.task_management.first_backend.application.projects.models.ProjectUser;
import com.task_management.first_backend.application.tasks.models.Task;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;
    private String designatedRole;
    private String avatarUrl;

    /** Active workspace for /workspaces/current/* resolution (multi-workspace). */
    @Column(name = "active_workspace_id")
    private Long activeWorkspaceId;

    //user setting
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserSetting userSetting;
    //project management
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Project> project;

    //project users
    @OneToMany(mappedBy = "assignedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectUser> sentInvitations = new ArrayList<>();
    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectUser> receivedInvitations = new ArrayList<>();
    //tasks
    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> assignedTasks;
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> createdTasks;

    //notifications
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;
    @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> sentNotifications;

    //onboarding
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private OnboardingRequest onboarding;

    @CreationTimestamp
    private Date lastLoginAt;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(role.name())
        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
