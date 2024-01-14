package com.exchange.portal.exchangeportal.service.exchange;

import com.exchange.portal.exchangeportal.common.constant.CacheKeyConstant;
import com.exchange.portal.exchangeportal.common.constant.CurrencyEnum;
import com.exchange.portal.exchangeportal.common.db.mapper.ExchangeMapper;
import com.exchange.portal.exchangeportal.common.db.po.ExchangePO;
import com.exchange.portal.exchangeportal.common.rest.CommonRestService;
import com.exchange.portal.exchangeportal.common.vo.ExchangeData;
import com.exchange.portal.exchangeportal.common.vo.Result;
import com.github.jesse.l2cache.Cache;
import com.github.jesse.l2cache.spring.cache.L2CacheCacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.exchange.portal.exchangeportal.common.constant.CacheKeyConstant.RATE_CURRENCY_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final CommonRestService commonRestService;
    private final L2CacheCacheManager cacheManager;

    private final ExchangeMapper exchangeMapper;

    ExecutorService threadPool = Executors.newFixedThreadPool(CurrencyEnum.values().length);



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

        Map<Object, ExchangeData> map = new HashMap<>();

        for (CurrencyEnum currency : CurrencyEnum.values()) {
            CompletableFuture.runAsync(() -> {
                ExchangeData data = putExchangeRateCache(currency.name());
                map.put(currency.name(), data);
                Cache l2cache = (Cache) cacheManager.getCache(RATE_CURRENCY_KEY).getNativeCache();
                l2cache.batchPut(map);
            }, threadPool);
        }
    }


    public ExchangeData putExchangeRateCache(String currency) {
        Date date = Calendar.getInstance().getTime();
        Date yesterdayDate = new Date(date.getTime() - 24 * 60 * 60 * 1000);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d");
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("dataset", "TaiwanExchangeRate");
        //uriVariables.put("start_date", dateFormat.format(yesterdayDate));
        uriVariables.put("start_date", "2024-01-9");
        uriVariables.put("data_id", currency);
        List<ExchangeData> exchange = getRateExchange(uriVariables);
        if(exchange == null || exchange.isEmpty()) {
            return null;
        } else {
            return exchange.get(0);
        }
    }

    @Cacheable(value = CacheKeyConstant.RATE_CURRENCY_KEY, key = "#currency", sync = true)
    public ExchangeData queryExchangeRateCache(String currency) {
        return putExchangeRateCache(currency);
    }

    @Cacheable(value = CacheKeyConstant.EXCHANGE_MAPPER_KEY, key = "(#iban + #pageNum + #pageSize)", sync = true)
    public List<ExchangePO> queryExchangeMapper(String iban, Integer pageNum, Integer pageSize) {
        return exchangeMapper.listExchange(iban, pageNum, pageSize);
    }
}
