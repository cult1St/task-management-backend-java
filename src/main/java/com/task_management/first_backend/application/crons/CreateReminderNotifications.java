package com.task_management.first_backend.application.crons;

import com.task_management.first_backend.application.enums.NotificationType;
import com.task_management.first_backend.application.models.Task;
import com.task_management.first_backend.application.models.User;
import com.task_management.first_backend.application.services.NotificationService;
import com.task_management.first_backend.application.services.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CreateReminderNotifications {

    private final TaskService taskService;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 3600000) //  every 1 hour
    public void runReminderJob() {
        log.info("========== REMINDER JOB START ==========");

        try {
            processApproachingDeadlines();
            // processOverdueTasks();
        } catch (Exception e) {
            log.error("Error in reminder job", e);
        }

        log.info("========== REMINDER JOB END ==========");
    }

    // ================= APPROACHING TASKS =================
    private void processApproachingDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(3);

        processTasksInPages(now, endDate, true);
    }

    // ================= OVERDUE TASKS =================
    private void processOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        processTasksInPages(null, now, false);
    }

    // ================= PAGINATION HANDLER =================
    private void processTasksInPages(LocalDateTime start, LocalDateTime end, boolean isApproaching) {
        int page = 0;
        int size = 50;

        Page<Task> taskPage;

        do {
            taskPage = isApproaching
                    ? taskService.getExpiringTasks(start, end, page, size)
                    : taskService.getOverdueTasks(end, page, size);

            for (Task task : taskPage.getContent()) {
                createTaskNotification(isApproaching, task, LocalDateTime.now());
            }

            page++;
        } while (taskPage.hasNext());
    }

    // ================= NOTIFICATION CREATION =================
    private void createTaskNotification(boolean isApproaching, Task task, LocalDateTime currentTime) {

        User user = task.getAssignedTo();
        if (user == null) {
            return;
        }

        LocalDate today = LocalDate.now();

        String title;
        String description;

        if (isApproaching) {
            title = "Task Approaching Deadline";
            String timeLeft = getApproachingTaskTimeMessage(task.getDueDate(), currentTime);

            description = String.format(
                    "Dear %s, your task \"%s\" is approaching its deadline. You have %s.",
                    user.getFullName(),
                    task.getTitle(),
                    timeLeft
            );

            if (notificationService.checkForPrevSent(user, NotificationType.TASK_DEADLINE, today)) {
                return;
            }

            notificationService.createNotification(
                    user,
                    title,
                    description,
                    NotificationType.TASK_DEADLINE,
                    user
            );

        } else {
            title = "Task Deadline Overdue";
            String timeOverdue = getOverdueTaskTimeMessage(task.getDueDate(), currentTime);

            description = String.format(
                    "Dear %s, your task \"%s\" is overdue by %s.",
                    user.getFullName(),
                    task.getTitle(),
                    timeOverdue
            );

            if (notificationService.checkForPrevSent(user, NotificationType.TASK_EXPIRED, today)) {
                return;
            }

            notificationService.createNotification(
                    user,
                    title,
                    description,
                    NotificationType.TASK_EXPIRED,
                    user
            );
        }
    }

    // ================= TIME HELPERS =================
    private String getApproachingTaskTimeMessage(LocalDateTime dueDateTime, LocalDateTime currentDate) {

        if (currentDate.isAfter(dueDateTime)) {
            return "deadline has already passed";
        }

        if (currentDate.toLocalDate().isEqual(dueDateTime.toLocalDate())) {
            long hoursLeft = Duration.between(currentDate, dueDateTime).toHours();

            if (hoursLeft <= 0) {
                return "less than an hour left";
            }

            return hoursLeft + (hoursLeft == 1 ? " hour left" : " hours left");
        }

        long daysLeft = Duration.between(currentDate, dueDateTime).toDays();
        return daysLeft + (daysLeft == 1 ? " day left" : " days left");
    }

    private String getOverdueTaskTimeMessage(LocalDateTime dueDateTime, LocalDateTime currentDate) {

        if (dueDateTime.isAfter(currentDate)) {
            return "not yet due";
        }

        if (currentDate.toLocalDate().isEqual(dueDateTime.toLocalDate())) {
            long hoursOverdue = Duration.between(dueDateTime, currentDate).toHours();

            if (hoursOverdue <= 0) {
                return "less than an hour";
            }

            return hoursOverdue + (hoursOverdue == 1 ? " hour" : " hours");
        }

        long daysOverdue = Duration.between(dueDateTime, currentDate).toDays();
        return daysOverdue + (daysOverdue == 1 ? " day" : " days");
    }
}