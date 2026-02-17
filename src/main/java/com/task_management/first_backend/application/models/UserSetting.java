package com.task_management.first_backend.application.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean taskAssignments;
    private boolean deadlineReminders;
    private boolean teamActivity;
    private boolean weeklyDigestEmail;

    private boolean twoFactorAuth;
    private boolean loginAlerts;

    private boolean compactSidebar;
    private boolean reduceMotion;

    private boolean githubConnected;
    private boolean slackConnected;
    private boolean jiraConnected;

    private String workspaceName;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}