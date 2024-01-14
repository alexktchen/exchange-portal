package com.exchange.portal.exchangeportal.common.vo;

import com.exchange.portal.exchangeportal.common.jackson.BaseJsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonView(BaseJsonView.class)
public class ExchangeVO {

    private String accountIban;

    private BigDecimal amount;
    private BigDecimal localeAmount;

    private String currency;

    private Timestamp valueDate;

    private String description;

    @JsonIgnore
    private Long total;

    private String locale;
}
