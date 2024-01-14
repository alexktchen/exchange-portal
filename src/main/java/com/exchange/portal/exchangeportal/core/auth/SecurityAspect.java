package com.exchange.portal.exchangeportal.core.auth;


import com.exchange.portal.exchangeportal.common.exception.UnAuthorizedException;
import com.exchange.portal.exchangeportal.common.vo.LoginInfoVO;
import com.exchange.portal.exchangeportal.common.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    @Around("@annotation(com.exchange.portal.exchangeportal.common.auth.UserAuth)")
    public Object userAccessControl(ProceedingJoinPoint joinPoint) throws Throwable {
        Optional<LoginInfoVO> opt = Stream.of(joinPoint.getArgs()).filter(this::filterLoginInfo).map(obj -> (LoginInfoVO) obj).findFirst();
        if (!opt.isPresent()) {
            throw new UnAuthorizedException();
        }
        if (opt.get().getAccountIban() == null || opt.get().getAccountIban().isEmpty()) {
            throw new UnAuthorizedException();
        }
        return joinPoint.proceed(joinPoint.getArgs());
    }

    private boolean filterLoginInfo(Object object) {
        return object instanceof LoginInfoVO;
    }
}
