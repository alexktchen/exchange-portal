package com.exchange.portal.exchangeportal.common.db.po;

import com.exchange.portal.exchangeportal.common.vo.ExchangeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangePO implements Serializable {

    private String uid;

    private String accountIban;

    private BigDecimal amount;

    private String currency;

    private Timestamp valueDate;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Timestamp createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Timestamp updateTime;

    private Long total;

    public ExchangeVO toVO() {
        return ExchangeVO.builder()
                .accountIban(this.getAccountIban())
                .amount(this.getAmount())
                .currency(this.getCurrency())
                .valueDate(this.getValueDate())
                .description(this.getDescription())
                .total(this.getTotal())
                .build();
    }
}
