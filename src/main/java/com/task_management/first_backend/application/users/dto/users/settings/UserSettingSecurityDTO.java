package com.task_management.first_backend.application.users.dto.users.settings;

import lombok.Data;

@Data
public class UserSettingSecurityDTO {
    private boolean twoFactorAuth;
    private boolean loginAlerts;
}
