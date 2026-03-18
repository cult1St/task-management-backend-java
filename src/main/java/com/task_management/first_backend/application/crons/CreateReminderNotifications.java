package com.task_management.first_backend.application.crons;


import com.task_management.first_backend.application.services.NotificationService;
import com.task_management.first_backend.application.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CreateReminderNotifications {
    private TaskService taskService;
    private NotificationService notificationService;

    public void processApprochingDeadline(){

    }
}


