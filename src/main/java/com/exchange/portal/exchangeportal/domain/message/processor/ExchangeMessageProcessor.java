package com.exchange.portal.exchangeportal.domain.message.processor;

import com.exchange.portal.exchangeportal.common.constant.KafkaTopic;
import com.exchange.portal.exchangeportal.common.constant.kafka.ExchangePortalTopicEnum;
import com.exchange.portal.exchangeportal.common.db.po.ExchangePO;
import com.exchange.portal.exchangeportal.domain.message.AbstractTopicMessageProcessor;
import com.exchange.portal.exchangeportal.domain.message.MessageHandler;
import com.exchange.portal.exchangeportal.util.JsonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(value = "message.receiver.enabled", havingValue = "true", matchIfMissing = true)
public class ExchangeMessageProcessor extends AbstractTopicMessageProcessor<ExchangePO> {

    private final List<MessageHandler<ExchangePO>> messageHandlerList;

    @Autowired
    public ExchangeMessageProcessor(List<MessageHandler<ExchangePO>> messageHandlerList) {
        this.messageHandlerList = messageHandlerList;
    }

    @Override
    public KafkaTopic getKafkaTopicInfo() {
        return ExchangePortalTopicEnum.EXCHANGE_EVENT;
    }

    @Override
    protected List<MessageHandler<ExchangePO>> listMessageHandler() {
        return messageHandlerList;
    }

    @Override
    public List<ExchangePO> deserialize(List<String> messageList) {
        if (CollectionUtils.isEmpty(messageList))
            return Collections.emptyList();

        return messageList.stream().map(message -> JsonUtils.readStringAsObject(message, ExchangePO.class)).collect(Collectors.toList());
    }
}
