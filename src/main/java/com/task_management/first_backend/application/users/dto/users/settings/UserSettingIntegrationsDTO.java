package com.task_management.first_backend.application.users.dto.users.settings;

import lombok.Data;

@Data
public class UserSettingIntegrationsDTO {
    private boolean githubConnected;
    private boolean slackConnected;
    private boolean jiraConnected;
}
