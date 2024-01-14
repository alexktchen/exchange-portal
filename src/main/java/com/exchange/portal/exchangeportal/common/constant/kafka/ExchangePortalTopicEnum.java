package com.exchange.portal.exchangeportal.common.constant.kafka;

import com.exchange.portal.exchangeportal.common.constant.KafkaTopic;
import lombok.Getter;

@Getter
public enum ExchangePortalTopicEnum implements KafkaTopic {
    EXCHANGE_EVENT(KafkaTopicConstants.EXCHANGE_EVENT, null);

    private String topic;
    private Class<?> clazz;

    ExchangePortalTopicEnum(String topic, Class<?> clazz) {
        this.topic = topic;
        this.clazz = clazz;
    }
}