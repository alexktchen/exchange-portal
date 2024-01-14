package com.exchange.portal.exchangeportal.domain.message;

import com.exchange.portal.exchangeportal.common.constant.KafkaTopic;
import com.exchange.portal.exchangeportal.config.message.ReceiverConfig;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
public abstract class AbstractTopicMessageProcessor<T> implements MessageDeserializer<T> {

    @Autowired
    private TaskExecutor exchangeTaskExecutor;

    public abstract KafkaTopic getKafkaTopicInfo();

    public String getKafkaConfig() {
        return ReceiverConfig.EXCHANGE_KAFKA;
    }

    protected abstract List<MessageHandler<T>> listMessageHandler();

    @KafkaListener(topics = "#{__listener.getKafkaTopicInfo().getTopic()}"
            , containerFactory = ReceiverConfig.EXCHANGE_KAFKA
            , groupId = "#{'${spring.kafka.consumer.group-id}' + '-' + '${env.name}' + '-' + '${addition_env_name}' + __listener.getIdSuffix()}")
    public CompletableFuture<Void> receive(List<ConsumerRecord<String, String>> message, Acknowledgment acknowledgment) {
        List<CompletableFuture<Void>> futureList = Lists.newArrayList();

        futureList.add(asyncProcess(message));

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        try {
            allFuture.get();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            log.error("Asynchronously processing lekima topic messages error: {}", message);
        }
        acknowledgment.acknowledge();
        return allFuture;
    }

    private CompletableFuture<Void> asyncProcess(List<ConsumerRecord<String, String>> records) {
        return CompletableFuture.runAsync(new TopicProcessAsyncTask(records), exchangeTaskExecutor);
    }

    @Getter
    public class TopicProcessAsyncTask implements Runnable {
        private final List<ConsumerRecord<String, String>> records;

        private TopicProcessAsyncTask(List<ConsumerRecord<String, String>> records) {
            this.records = records;
        }

        public String getTopic() {
            return getKafkaTopicInfo().getTopic();
        }

        @Override
        public void run() {
            List<String> messages = records.stream().map(ConsumerRecord::value).collect(Collectors.toList());
            process(messages);
        }
    }

    public final void process(List<String> message) {
        List<CompletableFuture<Void>> futureList = Lists.newArrayList();
        try {
            List<T> data = deserialize(message);
            listMessageHandler().forEach(h -> {
                        try {
                            futureList.add(h.handle(data));
                        } catch (Exception e) {
                            log.error("topic: {} message: {} exception: {}", getKafkaTopicInfo(), message, ExceptionUtils.getStackTrace(e));
                        }
                    }
            );
        } catch (Exception e) {
            log.error("topic: {} message: {} exception: {}", getKafkaTopicInfo(), message, ExceptionUtils.getStackTrace(e));
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
    }

    public String getIdSuffix() {
        return "";
    }
}
