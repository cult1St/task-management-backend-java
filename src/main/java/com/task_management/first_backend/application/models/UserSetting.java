package com.task_management.first_backend.application.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean taskAssignments = false;
    private boolean deadlineReminders = false;
    private boolean teamActivity = false;
    private boolean weeklyDigestEmail = false;

    private boolean twoFactorAuth = false;
    private boolean loginAlerts = false;

    private boolean compactSidebar = false;
    private boolean reduceMotion = false;

    private boolean githubConnected = false;
    private boolean slackConnected = false;
    private boolean jiraConnected = false;

    private String workspaceName;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
}