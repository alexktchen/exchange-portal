package com.exchange.portal.exchangeportal.service.exchange;

import com.exchange.portal.exchangeportal.common.db.po.ExchangePO;
import com.exchange.portal.exchangeportal.common.vo.ExchangeVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ExchangeService {

    void addExchange(List<ExchangePO> exchangePOList);

    PageInfo<ExchangeVO> queryExchange(String iban, String locale, Integer pageNum, Integer pageSize);
}
