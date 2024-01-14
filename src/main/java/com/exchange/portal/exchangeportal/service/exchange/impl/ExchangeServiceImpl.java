package com.exchange.portal.exchangeportal.service.exchange.impl;

import com.exchange.portal.exchangeportal.common.constant.LocaleEnum;
import com.exchange.portal.exchangeportal.common.db.mapper.ExchangeMapper;
import com.exchange.portal.exchangeportal.common.db.po.ExchangePO;
import com.exchange.portal.exchangeportal.common.vo.ExchangeData;
import com.exchange.portal.exchangeportal.common.vo.ExchangeVO;
import com.exchange.portal.exchangeportal.service.exchange.ExchangeService;
import com.exchange.portal.exchangeportal.service.redis.RedisService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeMapper exchangeMapper;

    private final RedisService redisService;

    private final static String RATE_CURRENCY_KEY = "EXCHANGE_RATE";

    @Override
    public void addExchange(List<ExchangePO> exchangePOList) {
        exchangeMapper.addExchange(exchangePOList);
    }

    @Override
    public PageInfo<ExchangeVO> queryExchange(String iban, String locale, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ExchangePO> items = exchangeMapper.listExchange(iban, pageNum, pageSize);
        Page<ExchangeVO> voItems = new Page<>();

        List<String> idList = Arrays.stream(LocaleEnum.values()).map(LocaleEnum::getCurrency).collect(Collectors.toList());
        Map<String, ExchangeData> exchangeData = redisService.getCacheMultiMap(RATE_CURRENCY_KEY, idList, ExchangeData.class);

        if(exchangeData == null || exchangeData.isEmpty()) {
            return new PageInfo<>(new Page<>(), 5);
        }

        items.forEach(item -> {
            ExchangeVO exchangeVO = item.toVO();
            voItems.add(calculateLocaleCurrency(exchangeVO, exchangeData, item.getCurrency(), item.getAmount(), locale));
        });

        return new PageInfo<>(voItems, 5);
    }

    public ExchangeVO calculateLocaleCurrency(ExchangeVO exchangeVO, Map<String, ExchangeData> exchangeData, String currency, BigDecimal amount, String locale) {
        if (currency.toLowerCase().equals(LocaleEnum.ZH_TW.getFieldName())) {
            exchangeVO.setLocaleAmount(amount);
        } else {
            ExchangeData exchangeCache = exchangeData.get(currency);
            exchangeVO.setLocaleAmount(amount.multiply(exchangeCache.getCash_sell()));
        }

        // If the locale of API call is different with TW, need to transfer to the locale currency
        if (!locale.toLowerCase().equals(LocaleEnum.ZH_TW.getFieldName())) {
            String localeCurrency = LocaleEnum.getCurrencyFromLocale((locale));
            ExchangeData exchangeCache = exchangeData.get(localeCurrency);
            exchangeVO.setLocaleAmount(exchangeVO.getLocaleAmount().divide(exchangeCache.getCash_sell(), 3, RoundingMode.HALF_UP));
        }
        exchangeVO.setLocale(locale);
        return exchangeVO;
    }

}
