package com.task_management.first_backend.application.services;

import com.task_management.first_backend.application.dto.UserResponseDTO;
import com.task_management.first_backend.application.dto.auth.LoginRequestDTO;
import com.task_management.first_backend.application.dto.users.UpdateUserRequestDTO;
import com.task_management.first_backend.application.dto.users.UserSettingsRequestDTO;
import com.task_management.first_backend.application.dto.users.settings.*;
import com.task_management.first_backend.application.enums.UserRole;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.models.UserSetting;
import com.task_management.first_backend.application.repositories.UserRepository;
import com.task_management.first_backend.application.repositories.UserSettingRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserSettingRepository userSettingRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String fullName, String email, String password) throws Exception{
        //check if a user exists with this email
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("User with this email already exists");
        }
        User createdUser = User
                .builder()
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(UserRole.USER)
                .build();
        return userRepository.save(createdUser);
    }

    public void loginTimeStamp(User user){
        user.setLastLoginAt(new Date());
        userRepository.save(user);
    }

    public User updateUser(UpdateUserRequestDTO request, Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update only non-null fields (safe partial update)

        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        System.out.println(request.getFullName() + request.getEmail());

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }

        if(request.getRoleTitle() != null && !request.getRoleTitle().isEmpty()){
            user.setDesignatedRole(request.getRoleTitle());
        }

        if(request.getAvatarUrl() != null){
            user.setAvatarUrl(request.getAvatarUrl());
        }

        return userRepository.save(user);
    }

    public UserSetting getUserSettings(User user){
        //check if user has a previous setting
        UserSetting userSetting = userSettingRepository.findByUserId(user.getId());
        if(userSetting == null){
            userSetting = new UserSetting();
        }
        return userSetting;
    }

    public UserSetting updateUserSetting(User user, UserSettingsRequestDTO request){
        //check if user has a previous setting
        UserSetting userSetting = userSettingRepository.findByUserId(user.getId());
        //create if user setting is null
        if (userSetting == null) {
            userSetting = new UserSetting();
            userSetting.setUser(user);
        }
        //only set or update non-null fields
       return userSetting;
    }

    public UserSettingNotificationsDTO updateUserNotificationsSetting(User user, UserSettingNotificationsDTO request){
        //check if user has a previous setting
        UserSetting userSetting = userSettingRepository.findByUserId(user.getId());
        //create if user setting is null
        if (userSetting == null) {
            userSetting = new UserSetting();
            userSetting.setUser(user);
        }
        userSetting.setTaskAssignments(request.isTaskAssignments());
        userSetting.setDeadlineReminders(request.isDeadlineReminders());
        userSetting.setTeamActivity(request.isTeamActivity());
        userSetting.setWeeklyDigestEmail(request.isWeeklyDigestEmail());
        userSettingRepository.save(userSetting);
        return request;
    }

    public UserSettingSecurityDTO updateUserSecuritySetting(User user, UserSettingSecurityDTO request) {

        UserSetting userSetting = userSettingRepository.findByUserId(user.getId());

        if (userSetting == null) {
            userSetting = new UserSetting();
            userSetting.setUser(user);
        }

        userSetting.setTwoFactorAuth(request.isTwoFactorAuth());
        userSetting.setLoginAlerts(request.isLoginAlerts());

        userSettingRepository.save(userSetting);

        return request;
    }

    public UserSettingAppearanceDTO updateUserAppearanceSetting(User user, UserSettingAppearanceDTO request) {

        UserSetting userSetting = userSettingRepository.findByUserId(user.getId());

        if (userSetting == null) {
            userSetting = new UserSetting();
            userSetting.setUser(user);
        }

        userSetting.setCompactSidebar(request.isCompactSidebar());
        userSetting.setReduceMotion(request.isReduceMotion());

        userSettingRepository.save(userSetting);

        return request;
    }

    public UserSettingIntegrationsDTO updateUserIntegrationsSetting(User user, UserSettingIntegrationsDTO request) {

        UserSetting userSetting = userSettingRepository.findByUserId(user.getId());

        if (userSetting == null) {
            userSetting = new UserSetting();
            userSetting.setUser(user);
        }

        userSetting.setGithubConnected(request.isGithubConnected());
        userSetting.setSlackConnected(request.isSlackConnected());
        userSetting.setJiraConnected(request.isJiraConnected());

        userSettingRepository.save(userSetting);

        return request;
    }

    public UserSettingWorkspaceDTO updateUserWorkspaceSetting(User user, UserSettingWorkspaceDTO request) {

        UserSetting userSetting = userSettingRepository.findByUserId(user.getId());

        if (userSetting == null) {
            userSetting = new UserSetting();
            userSetting.setUser(user);
        }

        userSetting.setWorkspaceName(request.getWorkspaceName());

        userSettingRepository.save(userSetting);

        return request;
    }

    public Page<UserResponseDTO> searchUsers(String query, User user, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        query = query != null ? query : "";
        return userRepository.findByIdNotAndFullNameContainingIgnoreCaseOrIdNotAndEmailContainingIgnoreCase(
                user.getId(),
                query,
                user.getId(),
                query,
                pageable
        ).map(this::mapToDto);
    }

    private UserResponseDTO mapToDto(User user){
        return new UserResponseDTO(user);
    }



}
