package com.task_management.first_backend.application.users.repositories;

import com.task_management.first_backend.application.users.models.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {
    public UserSetting findByUserId(Long userId);
}
