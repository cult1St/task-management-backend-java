package com.task_management.first_backend.application.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncExecutorConfig {

    @Bean(name = "emailExecutor")
    public Executor emailExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);  //thread always alive
        taskExecutor.setMaxPoolSize(10);      // max threads
        taskExecutor.setQueueCapacity(100);   // queued tasks
        taskExecutor.setThreadNamePrefix("Email-Async-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
