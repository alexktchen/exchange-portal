package com.exchange.portal.exchangeportal.service.auth;

import com.exchange.portal.exchangeportal.common.vo.LoginInfoVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

public interface AuthUserService {

    /**
     * Get login info
     *
     * @param httpServletRequest 請求資訊
     * @return LoginInfoVO 登入使用者資訊
     */
    LoginInfoVO getLoginInfo(HttpServletRequest httpServletRequest);

    /**
     * 由Token取得登入資訊
     *
     * @param token token
     * @return 登入資訊
     */
    LoginInfoVO getLoginInfo(String token);

    /**
     * 擷取token字串
     *
     * @param header header帶前綴的字串
     * @return token
     */
    String extractToken(String header);

}
