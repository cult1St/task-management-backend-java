package com.task_management.first_backend.application.crons;


import com.task_management.first_backend.application.dto.tasks.TaskDTO;
import com.task_management.first_backend.application.services.NotificationService;
import com.task_management.first_backend.application.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CreateReminderNotifications {
    private TaskService taskService;
    private NotificationService notificationService;

    public void processApproachingDeadline(){
        //define start and end date periods within now and three days time
        LocalDateTime startDate = LocalDate.now().atStartOfDay();
        LocalDateTime endDate = LocalDate.now().plusDays(3).atStartOfDay();
        //get approaching deadlines but eager load them to avoid excess memory usage
        long totalCount = taskService.getExpiringTasksCount(startDate, endDate);
        long processed;
        int batch = 50;
        if(totalCount < 50){
            //get all data and process them accurately
            List<TaskDTO> tasks = taskService.getExpiringTasks(startDate, endDate, 0, 50);
            for (TaskDTO task: tasks){
                //process notifications
            }
        }else{
            Long pages = (totalCount / batch);
            for(processed = 0; processed <= (totalCount / batch); processed++){
                List<TaskDTO> tasks = taskService.getExpiringTasks(startDate, endDate, 0, 50);
            }
        }
    }
}


