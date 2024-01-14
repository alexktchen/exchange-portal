package com.exchange.portal.exchangeportal.core.auth;

import com.exchange.portal.exchangeportal.common.auth.UserAuth;
import com.exchange.portal.exchangeportal.common.constant.ApiParameterConstants;
import com.exchange.portal.exchangeportal.common.constant.AuthSystemConstants;
import com.exchange.portal.exchangeportal.common.vo.LoginInfoVO;
import com.exchange.portal.exchangeportal.service.auth.AuthUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


@Slf4j
@Component
public class AuthUserInterceptor implements HandlerInterceptor {

    private AuthUserService authUserService;

    public AuthUserInterceptor(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            UserAuth userAuth = method.getAnnotation(UserAuth.class);
            if (userAuth == null || !userAuth.required()) {
                return true;
            } else if (userAuth.required() && userAuth.beEmpty()) {
                if (StringUtils.isBlank(httpServletRequest.getHeader(AuthSystemConstants.JWT_TOKEN_HEADER))) {
                    return true;
                }
            }
            LoginInfoVO loginInfoVO = authUserService.getLoginInfo(httpServletRequest);
            httpServletRequest.setAttribute(ApiParameterConstants.USER_LOGIN_INFO, loginInfoVO);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse, Object o, Exception e) {
    }

}
