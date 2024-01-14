package com.exchange.portal.exchangeportal.common.constant;

import lombok.Getter;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
public enum LocaleEnum {

    ZH_CN(Locale.CHINA, "cn", "CNY"),
    EN_US(Locale.US, "en", "USD"),
    ZH_TW(new Locale("zh", "TW"), "tw", "TWD"),
    ID_ID(new Locale("id", "ID"), "in", "IDR"),
    VI_VN(new Locale("vi", "VN"), "vn", "VND"),
    JA_JP(new Locale("ja", "JP"), "ja", "JPY"),
    KO_KR(new Locale("ko", "KR"), "ko", "KRW"),
    ZH_HK(new Locale("hk", "HK"), "hk", "HKD");


    private Locale locale;
    private String fieldName;
    private String currency;

    LocaleEnum(Locale locale, String fieldName, String currency) {
        this.locale = locale;
        this.fieldName = fieldName;
        this.currency = currency;
    }

    public static LocaleEnum valueOfDefault(String locale) {
        return LocaleEnum.EN_US;
    }

    public String getLocaleLanguage() {
        return getLocaleLanguage(locale);
    }

    public static String getLocaleLanguage(Locale locale) {
        return StringUtils.lowerCase(locale.getCountry());
    }

    public static String getCurrencyFromLocale(String locale) {
        Optional<LocaleEnum> item = Strings.isNotBlank(locale) ? enumMapper(LocaleEnum.class, e -> e.getLocaleLanguage().toLowerCase().equals(locale.toLowerCase())) : Optional.empty();
        if(item.isPresent()) {
            return item.get().getCurrency();
        } else {
            return Strings.EMPTY;
        }
    }

    public static <E extends Enum<E>> Optional<E> enumMapper(Class<E> enumClass, Predicate<? super E> predicate) {
        return EnumUtils.getEnumList(enumClass).stream().filter(predicate).findAny();
    }

}
