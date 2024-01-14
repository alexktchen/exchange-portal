package com.exchange.portal.exchangeportal.util;

import com.exchange.portal.exchangeportal.common.constant.LocaleEnum;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.TimeZone;


/**
 * 請求工具類
 */
@Slf4j
public class RequestUtils {

    public final static String HEADER_TIME_ZONE = "Time-Zone";

    public static HttpServletRequest getRequest() {
        HttpServletRequest request = null;
        if (RequestContextHolder.getRequestAttributes() != null)
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        return request;
    }

    public static LocaleEnum getLocaleEnum() {
        LocaleEnum localeEnum = LocaleEnum.ZH_TW;
        try {
            HttpServletRequest request = getRequest();
            String language = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);


            if (StringUtils.isNotBlank(language))
                localeEnum = LocaleEnum.valueOfDefault(language);

            return localeEnum;
        } catch (Exception e) {
            return localeEnum;
        }
    }

    public static TimeZone getTimeZone() {
        HttpServletRequest request = getRequest();
        if (Objects.isNull(request)) {
            return TimeZone.getTimeZone(DateUtils.DEFAULT_TIME_ZONE);
        }
        String timeZoneHeader = request.getHeader(HEADER_TIME_ZONE);
        if (Objects.isNull(timeZoneHeader)) {
            timeZoneHeader = DateUtils.DEFAULT_TIME_ZONE;
        }

        return TimeZone.getTimeZone(timeZoneHeader);
    }

}
