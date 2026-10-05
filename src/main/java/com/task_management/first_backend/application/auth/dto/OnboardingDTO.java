package com.task_management.first_backend.application.auth.dto;

import com.task_management.first_backend.application.auth.enums.OnboardingSteps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingDTO {
    private boolean completed;
    private OnboardingSteps currentStep;
    private boolean emailVerified;
    private boolean workspaceCreated;
    private boolean workspaceSetupCompleted;
    private boolean firstProjectCreated;
}
