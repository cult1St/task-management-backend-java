package com.task_management.first_backend.application.dto.users.settings;

import lombok.Data;

@Data
public class UserSettingNotificationsDTO {
    private boolean taskAssignments;
    private boolean deadlineReminders;
    private boolean teamActivity;
    private boolean weeklyDigestEmail;
}
