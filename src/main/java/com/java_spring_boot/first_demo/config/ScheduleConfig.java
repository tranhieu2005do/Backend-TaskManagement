package com.java_spring_boot.first_demo.config;

import com.java_spring_boot.first_demo.entity.Task;
import com.java_spring_boot.first_demo.repository.TaskRepository;
import com.java_spring_boot.first_demo.service.impl.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ScheduleConfig {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    @Transactional
    @Scheduled(cron = "0 0 8 * * ?") // chạy mỗi ngày lúc 8h sáng
    public void remindUpcomingTasks() {
        log.info("Reminding upcoming tasks...");
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Task> tasks = taskRepository.findTasksDueTomorrow(tomorrow, Task.Status.done);

        for (Task task : tasks) {
            notificationService.createReminderUpcomingTask(task);
        }
    }
}
