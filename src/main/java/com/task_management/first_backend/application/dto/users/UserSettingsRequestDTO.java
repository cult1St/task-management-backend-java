package com.task_management.first_backend.application.dto.users;

import com.task_management.first_backend.application.models.UserSetting;
import lombok.Data;

@Data
public class UserSettingsRequestDTO {
    private Notifications notifications;
    private Security security;
    private Appearance appearance;
    private Integrations integrations;
    private Workspace workspace;

    public UserSettingsRequestDTO(UserSetting setting) {
        this.notifications = new Notifications(setting);
        this.security = new Security(setting);
        this.appearance = new Appearance(setting);
        this.integrations = new Integrations(setting);
        this.workspace = new Workspace(setting);
    }

    @Data
    public static class Notifications {
        private boolean taskAssignments;
        private boolean deadlineReminders;
        private boolean teamActivity;
        private boolean weeklyDigestEmail;

        public Notifications(UserSetting setting) {
            this.taskAssignments = setting.isTaskAssignments();
            this.deadlineReminders = setting.isDeadlineReminders();
            this.teamActivity = setting.isTeamActivity();
            this.weeklyDigestEmail = setting.isWeeklyDigestEmail();
        }
    }

    @Data
    public static class Security {
        private boolean twoFactorAuth;
        private boolean loginAlerts;

        public Security(UserSetting setting) {
            this.twoFactorAuth = setting.isTwoFactorAuth();
            this.loginAlerts = setting.isLoginAlerts();
        }
    }

    @Data
    public static class Appearance {
        private boolean compactSidebar;
        private boolean reduceMotion;

        public Appearance(UserSetting setting) {
            this.compactSidebar = setting.isCompactSidebar();
            this.reduceMotion = setting.isReduceMotion();
        }
    }

    @Data
    public static class Integrations {
        private boolean githubConnected;
        private boolean slackConnected;
        private boolean jiraConnected;

        public Integrations(UserSetting setting) {
            this.githubConnected = setting.isGithubConnected();
            this.slackConnected = setting.isSlackConnected();
            this.jiraConnected = setting.isJiraConnected();
        }
    }

    @Data
    public static class Workspace {
        private String workspaceName;

        public Workspace(UserSetting setting) {
            this.workspaceName = setting.getWorkspaceName();
        }
    }
}
