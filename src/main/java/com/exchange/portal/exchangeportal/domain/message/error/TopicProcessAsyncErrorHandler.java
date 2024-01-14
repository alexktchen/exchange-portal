package com.exchange.portal.exchangeportal.domain.message.error;

import com.exchange.portal.exchangeportal.domain.message.AbstractTopicMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class TopicProcessAsyncErrorHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (r instanceof AbstractTopicMessageProcessor.TopicProcessAsyncTask) {
            AbstractTopicMessageProcessor.TopicProcessAsyncTask topicProcessAsyncTask = (AbstractTopicMessageProcessor.TopicProcessAsyncTask) r;
            log.warn("ThreadPool fully loaded, topic {}'s messages abandoned: {}", topicProcessAsyncTask.getTopic(), topicProcessAsyncTask.getRecords());
        }
    }
}
