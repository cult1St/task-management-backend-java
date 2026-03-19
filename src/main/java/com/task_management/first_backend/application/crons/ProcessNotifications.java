package com.task_management.first_backend.application.crons;

import com.task_management.first_backend.application.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@RequiredArgsConstructor
public class ProcessNotifications {
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 1000)
    public void processPendingNotifications(){
        System.out.println("Processing 100 notifications");
        Date start = new Date();
        notificationService.processNotificationsSending();
        Date end = new Date();
        long difference = (end.getTime() - start.getTime()) / 1000;
        System.out.println("Processed 100 notifications. Took " + difference + " seconds");
    }

}
