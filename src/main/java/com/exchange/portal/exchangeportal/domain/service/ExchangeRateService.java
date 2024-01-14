package com.exchange.portal.exchangeportal.domain.service;

import com.exchange.portal.exchangeportal.common.constant.CurrencyEnum;
import com.exchange.portal.exchangeportal.common.rest.CommonRestService;
import com.exchange.portal.exchangeportal.common.vo.ExchangeData;
import com.exchange.portal.exchangeportal.common.vo.Result;
import com.exchange.portal.exchangeportal.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {
    //private final ServerHost serverHost;
    private final RedisService redisService;
    private final CommonRestService commonRestService;

    private final static String RATE_CURRENCY_KEY = "EXCHANGE_RATE";

    private static final ParameterizedTypeReference<Result<List<ExchangeData>>> CURRENCY_PRICE_TYPE = new ParameterizedTypeReference<Result<List<ExchangeData>>>() {
    };

    public List<ExchangeData> getRateExchange(Map<String, String> uriVariables) {
        long start = System.currentTimeMillis();
        String url = "https://api.finmindtrade.com/api/v4/data?dataset={dataset}&data_id={data_id}&start_date={start_date}";
        Optional<List<ExchangeData>> mapOpt = commonRestService.getForResult(url, CURRENCY_PRICE_TYPE, uriVariables);
        log.info("post api:{}, res:{}", url, mapOpt);
        log.info("Elapsed time: " + (System.currentTimeMillis() - start));
        return mapOpt.orElse(null);
    }

    public void initRateMap() {
        try {
            ExecutorService threadPool = Executors.newFixedThreadPool(CurrencyEnum.values().length);

            Date date = Calendar.getInstance().getTime();
            Date yesterdayDate = new Date(date.getTime() - 24 * 60 * 60 * 1000);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d");


            for(CurrencyEnum currency : CurrencyEnum.values()) {
                CompletableFuture.runAsync(() -> {
                    Map<String, String> uriVariables = new HashMap<>();
                    uriVariables.put("dataset", "TaiwanExchangeRate");
                    uriVariables.put("start_date", dateFormat.format(yesterdayDate));
                    //uriVariables.put("start_date", "2024-01-9");
                    uriVariables.put("data_id", currency.name());
                    List<ExchangeData> exchange = getRateExchange(uriVariables);

                    if(!exchange.isEmpty()) {
                        Map<String, ExchangeData> rateMap = new HashMap<>();
                        rateMap.put(currency.name(), exchange.get(0));
                        setExchangeRateCache(rateMap);
                    }

                }, threadPool);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void setExchangeRateCache(Map<String, ExchangeData> rateMap) {
        if (MapUtils.isEmpty(rateMap)) {
            return;
        }
        redisService.setCacheHash(RATE_CURRENCY_KEY, rateMap);
    }

}
