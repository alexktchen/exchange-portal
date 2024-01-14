package com.exchange.portal.exchangeportal.config;

import com.exchange.portal.exchangeportal.common.MDCTaskDecorator;
import com.exchange.portal.exchangeportal.domain.message.error.TopicProcessAsyncErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    public final static MDCTaskDecorator taskDecorator = MDCTaskDecorator.getInstance();
    public final static String ASYNC_POOL = "business-async";
    public final static String BASIC_INFO_ASYNC_POOL = "business-basicInfo-async";
    public final static String OUTRIGHT_POOL = "outright-async";

    @Bean(name = "history-thread-pool")
    public ThreadPoolExecutor oddsDetailGetter() {
        return new ThreadPoolExecutor(3, 6, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new CustomizableThreadFactory("history-thread-pool-"));
    }

    @Primary
    @Bean(ASYNC_POOL)
    public TaskExecutor asyncTaskThreadPool(TopicProcessAsyncErrorHandler topicProcessAsyncErrorHandler) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(32);
        executor.setMaxPoolSize(128);
        executor.setQueueCapacity(192);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Async-business-");
        executor.setRejectedExecutionHandler(topicProcessAsyncErrorHandler);
        executor.setTaskDecorator(taskDecorator);
        return executor;
    }

    @Bean(BASIC_INFO_ASYNC_POOL)
    public TaskExecutor basicInfoAsyncTaskThreadPool(TopicProcessAsyncErrorHandler topicProcessAsyncErrorHandler) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(30);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Async-business-basic-info");
        executor.setRejectedExecutionHandler(topicProcessAsyncErrorHandler);
        executor.setTaskDecorator(taskDecorator);
        return executor;
    }

}
