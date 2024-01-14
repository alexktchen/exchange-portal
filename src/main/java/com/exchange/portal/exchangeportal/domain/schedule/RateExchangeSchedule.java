package com.exchange.portal.exchangeportal.domain.schedule;

import com.exchange.portal.exchangeportal.service.exchange.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateExchangeSchedule {
    private final ExchangeRateService exchangeRateService;

    private static final String EXCHANGE_RATE_LOCK = "CURRENCY_PRICE_LOCK";

    //@Scheduled(cron = "0 0 0/1 * * ?", zone = "GMT+8")
    @Scheduled(fixedDelay = Long.MAX_VALUE)
    @SchedulerLock(name = EXCHANGE_RATE_LOCK, lockAtLeastFor = "30s", lockAtMostFor = "50s")
    public void initUsdRateSchedule() {
        exchangeRateService.initRateMap();
    }
}

