package com.exchange.portal.exchangeportal.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(hidden = true)
    private String accountIban;
    private String locale;
}
