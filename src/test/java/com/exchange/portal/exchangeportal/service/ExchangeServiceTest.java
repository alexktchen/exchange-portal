package com.exchange.portal.exchangeportal.service;

import com.exchange.portal.exchangeportal.common.db.mapper.ExchangeMapper;
import com.exchange.portal.exchangeportal.common.vo.ExchangeData;
import com.exchange.portal.exchangeportal.common.vo.ExchangeVO;
import com.exchange.portal.exchangeportal.service.exchange.impl.ExchangeServiceImpl;
import com.exchange.portal.exchangeportal.service.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExchangeServiceTest.class)
public class ExchangeServiceTest {

    @InjectMocks
    private ExchangeServiceImpl exchangeService;


    @Test
    public void testCalculateLocaleCurrency() {
        ExchangeVO exchangeVO = new ExchangeVO();
        exchangeVO.setAmount(new BigDecimal(100));
        exchangeVO.setCurrency("USD");
        exchangeVO.setLocale("tw");

        ExchangeData exchange = new ExchangeData();
        exchange.setCash_sell(BigDecimal.valueOf(31.145));

        Map<String, ExchangeData> exchangeData = new HashMap<>();
        exchangeData.put("USD", exchange);

        ExchangeVO expectedExchangeVO = exchangeService.calculateLocaleCurrency(exchangeVO, exchangeData, exchangeVO.getCurrency(), exchangeVO.getAmount(), exchangeVO.getLocale());
        Assertions.assertEquals(expectedExchangeVO.getLocaleAmount(), exchangeVO.getAmount().multiply(exchangeData.get(exchangeVO.getCurrency()).getCash_sell()));

    }

}
