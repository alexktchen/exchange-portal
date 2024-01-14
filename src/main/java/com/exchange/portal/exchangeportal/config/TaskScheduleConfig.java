package com.exchange.portal.exchangeportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class TaskScheduleConfig {

    private final static String SCHEDULE_POOL = "exchange-portal-schedule";

    @Bean(SCHEDULE_POOL)
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new MDCSchedulerTaskExecutor();
        taskScheduler.setPoolSize(20);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setThreadNamePrefix("Schedule-business-");
        return taskScheduler;
    }

}
