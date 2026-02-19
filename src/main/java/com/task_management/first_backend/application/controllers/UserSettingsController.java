package com.task_management.first_backend.application.controllers;

import com.task_management.first_backend.application.dto.SuccessResponse;
import com.task_management.first_backend.application.dto.users.UserSettingsResponseDTO;
import com.task_management.first_backend.application.dto.users.settings.*;
        import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/settings")
public class UserSettingsController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<SuccessResponse<UserSettingsResponseDTO>> getUserSettings(
            @AuthenticationPrincipal User authUser
    ) {
        UserSettingsResponseDTO response =
                new UserSettingsResponseDTO(
                        userService.getUserSettings(authUser)
                );

        return ResponseEntity.ok(
                SuccessResponse.of("User Settings Fetched Successfully", response)
        );
    }

    @PatchMapping("/notifications")
    public ResponseEntity<SuccessResponse<UserSettingNotificationsDTO>> updateNotifications(
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody UserSettingNotificationsDTO request
    ) {
        UserSettingNotificationsDTO response =
                userService.updateUserNotificationsSetting(authUser, request);

        return ResponseEntity.ok(
                SuccessResponse.of("User Notifications Settings Updated Successfully", response)
        );
    }

    @PatchMapping("/security")
    public ResponseEntity<SuccessResponse<UserSettingSecurityDTO>> updateSecurity(
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody UserSettingSecurityDTO request
    ) {
        UserSettingSecurityDTO response =
                userService.updateUserSecuritySetting(authUser, request);

        return ResponseEntity.ok(
                SuccessResponse.of("User Security Settings Updated Successfully", response)
        );
    }

    @PatchMapping("/appearance")
    public ResponseEntity<SuccessResponse<UserSettingAppearanceDTO>> updateAppearance(
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody UserSettingAppearanceDTO request
    ) {
        UserSettingAppearanceDTO response =
                userService.updateUserAppearanceSetting(authUser, request);

        return ResponseEntity.ok(
                SuccessResponse.of("User Appearance Settings Updated Successfully", response)
        );
    }

    @PatchMapping("/integrations")
    public ResponseEntity<SuccessResponse<UserSettingIntegrationsDTO>> updateIntegrations(
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody UserSettingIntegrationsDTO request
    ) {
        UserSettingIntegrationsDTO response =
                userService.updateUserIntegrationsSetting(authUser, request);

        return ResponseEntity.ok(
                SuccessResponse.of("User Integrations Settings Updated Successfully", response)
        );
    }

    @PatchMapping("/workspace")
    public ResponseEntity<SuccessResponse<UserSettingWorkspaceDTO>> updateWorkspace(
            @AuthenticationPrincipal User authUser,
            @Valid @RequestBody UserSettingWorkspaceDTO request
    ) {
        UserSettingWorkspaceDTO response =
                userService.updateUserWorkspaceSetting(authUser, request);

        return ResponseEntity.ok(
                SuccessResponse.of("User Workspace Settings Updated Successfully", response)
        );
    }
}

