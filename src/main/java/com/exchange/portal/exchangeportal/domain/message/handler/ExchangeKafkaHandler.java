package com.exchange.portal.exchangeportal.domain.message.handler;

import com.exchange.portal.exchangeportal.common.constant.KafkaTopic;
import com.exchange.portal.exchangeportal.common.constant.kafka.ExchangePortalTopicEnum;
import com.exchange.portal.exchangeportal.common.db.po.ExchangePO;
import com.exchange.portal.exchangeportal.domain.message.MessageHandler;

import com.exchange.portal.exchangeportal.service.exchange.ExchangeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeKafkaHandler implements MessageHandler<ExchangePO> {

    private final ExchangeService exchangeService;

    @Getter
    private KafkaTopic kafkaTopicInfo = ExchangePortalTopicEnum.EXCHANGE_EVENT;

    @Override
    public CompletableFuture<Void> handle(List<ExchangePO> message) {
        log.info("MatchBulletinKafkaHandler message : {}", message);
        exchangeService.addExchange(message);
        return CompletableFuture.allOf();
    }

}
