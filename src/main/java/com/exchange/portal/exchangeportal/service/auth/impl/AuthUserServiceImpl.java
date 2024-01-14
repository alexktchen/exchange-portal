package com.exchange.portal.exchangeportal.service.auth.impl;


import com.exchange.portal.exchangeportal.common.auth.RawAccessJwtToken;
import com.exchange.portal.exchangeportal.common.constant.AuthSystemConstants;
import com.exchange.portal.exchangeportal.common.constant.CommonErrorTypes;
import com.exchange.portal.exchangeportal.common.constant.exception.ApiException;
import com.exchange.portal.exchangeportal.common.vo.LoginInfoVO;
import com.exchange.portal.exchangeportal.service.auth.AuthUserService;
import com.exchange.portal.exchangeportal.util.JsonUtils;
import com.exchange.portal.exchangeportal.util.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class AuthUserServiceImpl implements AuthUserService {



    public AuthUserServiceImpl() {

    }


    public String getTokenByLoginInfoVO(LoginInfoVO loginInfoVO) {
        return JwtTokenUtils.generateToken(JsonUtils.writeObjectAsString(loginInfoVO), AuthSystemConstants.JWT_SECRET,
                AuthSystemConstants.JWT_EXPIRATION);

    }

    @Override
    public LoginInfoVO getLoginInfo(HttpServletRequest httpServletRequest) {
//        LoginInfoVO vo = new LoginInfoVO();
//        vo.setAccountIban("CH93-0000-0000-0000-0000-0");
//        vo.setLocale("jp");
//        String token1 = getTokenByLoginInfoVO(vo);
        String token = extractToken(httpServletRequest.getHeader(AuthSystemConstants.JWT_TOKEN_HEADER));
        return getLoginInfo(token);
    }

    @Override
    public LoginInfoVO getLoginInfo(String token) {
        LoginInfoVO loginInfoVO;
        try {
            RawAccessJwtToken accessJwtToken = new RawAccessJwtToken(token, AuthSystemConstants.JWT_SECRET);
            String json = accessJwtToken.getTokenValue();
            loginInfoVO = JsonUtils.readStringAsObject(json, LoginInfoVO.class);
        } catch (Exception e) {
            throw ApiException.builder().errorType(CommonErrorTypes.INVALID_TOKEN).build();
        }

        if (loginInfoVO == null) {
            throw ApiException.builder().errorType(CommonErrorTypes.INVALID_TOKEN).build();
        }

        //TODO: check token info from cache


        return loginInfoVO;
    }

    @Override
    public String extractToken(String header) {
        if (StringUtils.isEmpty(header) || StringUtils.isBlank(header)) {
            throw ApiException.builder().errorType(CommonErrorTypes.INVALID_TOKEN).message("Authorization header cannot be blank").build();
        }
        if (!header.startsWith(AuthSystemConstants.JWT_HEADER_PREFIX) || header.length() <= AuthSystemConstants.JWT_HEADER_PREFIX.length()) {
            throw ApiException.builder().errorType(CommonErrorTypes.INVALID_TOKEN).message("Authorization header is error").build();
        }
        return header.substring(AuthSystemConstants.JWT_HEADER_PREFIX.length());
    }
}
