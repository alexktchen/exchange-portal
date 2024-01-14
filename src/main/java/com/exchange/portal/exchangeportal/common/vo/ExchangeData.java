package com.exchange.portal.exchangeportal.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeData implements Serializable {

    private BigDecimal cash_buy;
    private BigDecimal cash_sell;
    private BigDecimal spot_buy;
    private BigDecimal spot_sell;
    private String date;
    private String currency;


}
