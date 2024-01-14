package com.exchange.portal.exchangeportal.controller;

import com.exchange.portal.exchangeportal.common.auth.UserAuth;
import com.exchange.portal.exchangeportal.common.db.po.ExchangePO;
import com.exchange.portal.exchangeportal.common.exception.UnAuthorizedException;
import com.exchange.portal.exchangeportal.common.vo.ExchangeVO;
import com.exchange.portal.exchangeportal.common.vo.LoginInfoVO;
import com.exchange.portal.exchangeportal.common.vo.Result;
import com.exchange.portal.exchangeportal.service.exchange.ExchangeService;
import com.exchange.portal.exchangeportal.util.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "Get user exchange data")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class ExchangeController {

    private final ExchangeService exchangeService;

    @UserAuth(required = true)
    @GetMapping(value = "/user/exchange", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Result queryExchange(@RequestAttribute LoginInfoVO loginInfo,
                                @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) throws UnAuthorizedException {


        PageInfo<ExchangeVO> poList = exchangeService.queryExchange(loginInfo.getAccountIban(),loginInfo.getLocale(), pageNum, pageSize);

        return new Result<>(poList);

    }
}
