package com.exchange.portal.exchangeportal.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class DateUtils {

    public static final int SEC_DIGITS = 10;
    public static final int MS_DIGITS = 13;
    public static final String DEFAULT_TIME_ZONE = "GMT-0400";
    public final static ZoneId DEFAULT_ZONE_ID = ZoneId.of(DEFAULT_TIME_ZONE);
    public static final TimeZone DEFAULT_TIME_ZONE_OBJ = TimeZone.getTimeZone(DateUtils.DEFAULT_TIME_ZONE);
    public static final ZoneId CST = ZoneId.of("Asia/Taipei");


    public final static DateTimeFormatter yyyyMMddDash = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(DEFAULT_ZONE_ID);
    public final static DateTimeFormatter yyyyMMddHHmmDash = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(DEFAULT_ZONE_ID);
    public final static DateTimeFormatter yyyyMMddHHmmssDash = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(DEFAULT_ZONE_ID);
    public final static DateTimeFormatter yyyyMMddHHmmssDashCn = DateTimeFormatter.ofPattern("yyyy年[MM][M]月[dd][d]日 HH:mm").withZone(DEFAULT_ZONE_ID);
    public final static DateTimeFormatter yyyyMMddHHmmssDashEn = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH).withZone(DEFAULT_ZONE_ID);

    private final static Pattern yyyyMMddHHmmDash_pattern = Pattern.compile("^\\d{4}年\\d{1,2}月\\d{1,2}日 \\d{2}:\\d{2}$");
    private final static Pattern ddMMyyyyHHmm_pattern = Pattern.compile("\\d{1,2}\\s+[A-Za-z]{3,3}\\s+[19|20]+\\d{2,2}\\s+\\d{2,2}\\:\\d{2,2}");//24 Jul 2020 11:45​

    public static final DateTimeFormatter PATTERN_HHmmss = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final String START_OF_DAY = LocalTime.MIN.format(PATTERN_HHmmss);
    ;
    public static final String END_OF_DAY = LocalTime.MAX.format(PATTERN_HHmmss);
    
    public static final String DATE_AND_TIME_DASH_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_DASH_FORMAT = "yyyy-MM-dd";

    @Deprecated
    public static Date convertUnixTimestampToDate(String timestamp) {
        return null;
    }

    @Deprecated
    public static String addLastStartOfDay(String dt) {
        return null;
    }

    @Deprecated
    public static String addLastSecondOfDay(String dt) {
        return null;
    }

    @Deprecated
    public static Date dateParse(String date) throws ParseException {
        return null;
    }

    public static boolean dateFormatMatches(String date) {
        return yyyyMMddHHmmDash_pattern.matcher(date).matches();
    }

    public static boolean dateFormatEnMatches(String date){
        return ddMMyyyyHHmm_pattern.matcher(date).matches();
    }


    public static String formatDate(Date date) {
        ZonedDateTime d = ZonedDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
        return d.format(yyyyMMddDash);
    }

    public static String formatDateTime(Date date) {
        ZonedDateTime d = ZonedDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
        return d.format(yyyyMMddHHmmssDash);
    }

    public static long parseDateToMilli(String date) {
        return parseDate(date).toInstant().toEpochMilli();
    }

    public static ZonedDateTime parseDate(String date) {
        LocalDate parse = LocalDate.parse(date, yyyyMMddDash);
        return parse.atStartOfDay(DEFAULT_ZONE_ID);
    }

    public static ZonedDateTime parseDateTime(String dateTime) {
        LocalDateTime parse = LocalDateTime.parse(dateTime, yyyyMMddHHmmssDash);
        return parse.atZone(DEFAULT_ZONE_ID);
    }

    public static ZonedDateTime parseDateWithTimeZone(String date, TimeZone timezone) {
        LocalDate parse = LocalDate.parse(date, yyyyMMddDash);
        return parse.atStartOfDay(timezone.toZoneId());
    }

    public static ZonedDateTime parseDateTimeWithTimeZone(String dateTime, TimeZone timezone) {
        LocalDateTime parse = LocalDateTime.parse(dateTime, yyyyMMddHHmmssDash);
        return parse.atZone(timezone.toZoneId());
    }

    public static int daysBetween(ZonedDateTime dateTime1, ZonedDateTime dateTime2) {
        Period p = Period.between(dateTime1.toLocalDate(), dateTime2.toLocalDate());
        return p.getDays();
    }

    public static ZonedDateTime getCurrDateTime() {
        return ZonedDateTime.now(DEFAULT_ZONE_ID);
    }

    public static long getCurrEpochMilli() {
        return ZonedDateTime.now(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    public static ZonedDateTime getZonedDateTime(long epochMilli) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), DEFAULT_ZONE_ID);
    }

    public static boolean isDate1GreaterDate2(String date1, String date2) {
        return parseDateToMilli(date1) > parseDateToMilli(date2);
    }

    /**
     * 取得美東現在日期格式
     *
     * @return yyyy-MM-dd
     */
    public static String getCurrDate() {
        return ZonedDateTime.now(DEFAULT_ZONE_ID).format(yyyyMMddDash);
    }

    /**
     * 取得時區現在日期格式
     * @param zoneId 時區
     * @return yyyy-MM-dd
     */
    public static String getCurrDate(ZoneId zoneId) {
        return ZonedDateTime.now(zoneId).format(yyyyMMddDash);
    }

    /**
     * 增加天數
     *
     * @param dt  yyyy-MM-dd
     * @param day 增加天數
     * @return yyyy-MM-dd
     */
    public static String addDays(String dt, int day) {
        return LocalDate.parse(dt, yyyyMMddDash).plusDays(day).format(yyyyMMddDash);
    }

    /**
     * 取得指定日期帶有日期時間00:00的UnixTime
     *
     * @param date
     * @return [date] 00:00的UnixTime (millisecond)
     */
    public static long getStartOfDay(String date) {
        LocalDate localDate = LocalDate.parse(date, yyyyMMddDash);
        return localDate.atTime(LocalTime.MIN).atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }
    /**
     * 取得指定日期帶有日期時間23:59:59.999999999的UnixTime
     *
     * @param date
     * @return [date] 23:59:59.999999999的UnixTime (millisecond)
     */
    public static long getEndOfDay(String date) {
        LocalDate localDate = LocalDate.parse(date, yyyyMMddDash);
        return localDate.atTime(LocalTime.MAX).atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    public static long getTimestampFromStringDate(String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date, yyyyMMddHHmmDash);
        return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    public static long getTimestampFromStringDatetime(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, yyyyMMddHHmmssDash);
        return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    public static long getTimestampFromCnStringDate(String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date, yyyyMMddHHmmssDashCn);
        return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    public static long getTimestampFromEnStringDate(String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date, yyyyMMddHHmmssDashEn);
        return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 回傳美東時間格式
     *
     * @param millis EpochMilli
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String epochMilli2NYDatetimeFormatted(long millis) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), DEFAULT_ZONE_ID);
        return zonedDateTime.format(yyyyMMddHHmmssDash);
    }


    /**
     * 還原美東 timestamp 成年月日
     *
     * @param t EpochMilli
     * @return String
     */
    public static String epochMilli2NYDateFormatted(long t) {
        Instant i = Instant.ofEpochMilli(t);
        ZonedDateTime z = ZonedDateTime.ofInstant(i, DEFAULT_ZONE_ID);

        return z.format(yyyyMMddDash);
    }


    /**
     * 根據時區將日期轉換成美東時間
     *
     * @param date     yyyy-MM-dd
     * @param timeZone default:Asia/Taipei (https://en.wikipedia.org/wiki/List_of_tz_database_time_zones)
     * @return
     */
    public static ZonedDateTime parseDate(String date, String timeZone) {
        ZoneId zoneId = StringUtils.isBlank(timeZone) ? DateUtils.CST : getDefaultZoneId(timeZone);
        DateTimeFormatter dynamicyyyyMMddHHmmssDash = DateTimeFormatter.ofPattern(DateUtils.DATE_AND_TIME_DASH_FORMAT).withZone(zoneId);
        return ZonedDateTime.parse(addStartOfDay(date), dynamicyyyyMMddHHmmssDash).withZoneSameInstant(DEFAULT_ZONE_ID);

    }
    public static String addStartOfDay(String date) {
        return changeRequestDate(date + " " + START_OF_DAY);
    }

    private static String changeRequestDate(String date) {
        String timeZone = getHeaderTimeZone();
        if (StringUtils.isBlank(timeZone))
            return date;

        return getTimeZoneDate(date, DateUtils.DATE_AND_TIME_DASH_FORMAT, timeZone, DEFAULT_TIME_ZONE);
    }

    private static String getHeaderTimeZone() {
        HttpServletRequest request = RequestUtils.getRequest();
        if (request == null)
            return null;

        String timeZone = RequestUtils.getRequest().getHeader(RequestUtils.HEADER_TIME_ZONE);
        if (StringUtils.isBlank(timeZone))
            return null;

        return timeZone;
    }

    /**
     * 根據時區將時間轉換成美東時間
     *
     * @param dateTime yyyy-MM-dd HH:mm:ss
     * @param timeZone default:Asia/Taipei (https://en.wikipedia.org/wiki/List_of_tz_database_time_zones)
     * @return
     */
    public static ZonedDateTime parseDateTime(String dateTime, String timeZone) {
        ZoneId zoneId = StringUtils.isBlank(timeZone) ? DateUtils.CST : getDefaultZoneId(timeZone);
        DateTimeFormatter dynamicyyyyMMddHHmmssDash = DateTimeFormatter.ofPattern(DateUtils.DATE_AND_TIME_DASH_FORMAT).withZone(zoneId);
        return ZonedDateTime.parse(dateTime, dynamicyyyyMMddHHmmssDash).withZoneSameInstant(DEFAULT_ZONE_ID);
    }

    /**
     * 根據時區將時間轉換成美東時間
     *
     * @param dateTime yyyy-MM-dd HH:mm:ss
     * @param timeZone default:Asia/Taipei (https://en.wikipedia.org/wiki/List_of_tz_database_time_zones)
     * @return
     */
    public static String parseDateTimeStr(String dateTime, String timeZone) {
        ZonedDateTime zdt = parseDateTime(dateTime, timeZone);
        return zdt.format(yyyyMMddHHmmssDash);
    }

    /**
     * 將美東時間轉換成客戶端的時間字串
     * yyyy-MM-dd HH:mm:ss[America/New_York] -> yyyy-MM-dd[clientTimeZone]
     *
     * @param sourceDateTime
     * @param clientTimeZone default:Asia/Taipei (https://en.wikipedia.org/wiki/List_of_tz_database_time_zones)
     * @return
     */
    public static String parseDefaultDateTimeToClientDateStr(String sourceDateTime, String clientTimeZone) {
        ZonedDateTime zdt = parseDateTime(sourceDateTime, clientTimeZone);
        return parseDefaultDateTimeToClientDateStr(new Timestamp(zdt.toEpochSecond() * 1000L), clientTimeZone);

    }


    /**
     * 將 timestamp 轉換成客戶端的時間字串
     * timestamp -> yyyy-MM-dd[clientTimeZone]
     *
     * @param sourceDateTime
     * @param clientTimeZone default:Asia/Taipei (https://en.wikipedia.org/wiki/List_of_tz_database_time_zones)
     * @return
     */
    public static String parseDefaultDateTimeToClientDateStr(Timestamp sourceDateTime, String clientTimeZone) {
        ZoneId zoneId = StringUtils.isBlank(clientTimeZone) ? DateUtils.CST : getDefaultZoneId(clientTimeZone);
        ZonedDateTime zdt = ZonedDateTime.ofInstant(sourceDateTime.toInstant(), zoneId);
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(zoneId));

    }

    public static ZoneOffset getZoneOffset(String timeZone) {
        LocalDateTime dt = LocalDateTime.now();
        ZoneId zoneId = StringUtils.isBlank(timeZone) ? DateUtils.CST : getDefaultZoneId(timeZone);
        ZonedDateTime zdt = dt.atZone(zoneId);
        return zdt.getOffset();
    }

    public static List<ZonedDateTime> parseStartAndEndDateTime(String timeZone,
                                                               String startDate,
                                                               String endDate) {
        return Arrays.asList(
                parseDateTime(startDate + " " + START_OF_DAY, timeZone),
                parseDateTime(endDate + " " + END_OF_DAY, timeZone)
        );
    }

    public static List<String> parseStartAndEndDateTimeStr(String timeZone,
                                                           String startDate,
                                                           String endDate) {
        return Arrays.asList(
                parseDateTimeStr(startDate + " " + START_OF_DAY, timeZone),
                parseDateTimeStr(endDate + " " + END_OF_DAY, timeZone)
        );
    }

    /**
     * 20200202 -> 2020-02-05
     *
     * @param date
     * @return
     */
    public static String toDashDate(String date) {
        return String.format("%s-%s-%s", date.substring(0, 4), date.substring(4, 6), date.substring(6));
    }

    /**
     * 1589004000 -> 1589004000000
     *
     * @param epochSecond
     * @return
     */
    public static long epochSecondToEpochMilli(long epochSecond) {
        return epochSecond * 1000;
    }

    /**
     * Date to Timestamp
     */
    public static Timestamp dateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }


    public static String getTimeZoneDate(String date, String patten, String sourceTimeZoneStr, String targetTimeZoneStr) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(patten);
        ZonedDateTime sourceTime = ZonedDateTime.parse(date, dtf.withZone(getDefaultZoneId(sourceTimeZoneStr)));
        ZonedDateTime targetTime = sourceTime.withZoneSameInstant(getDefaultZoneId(targetTimeZoneStr));

        return targetTime.format(dtf.withZone(getDefaultZoneId(targetTimeZoneStr)));
    }

    public static long getUnixTime(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, yyyyMMddHHmmssDash);

        return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 將日期時間字串作為一天的開始時間計算一天的結束時間的UnixTime (因應分時區查詢),
     * 傳入時間皆視為預設時區(GMT-4)
     * @param startDateTime 一天的開始時間字串
     * @return UnixTime of a day end
     */
    public static long getEndOfDayUnixTime(String startDateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(startDateTime, yyyyMMddHHmmssDash);
        localDateTime = localDateTime.plusDays(1).minusSeconds(1);
        return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 將日期時間字串作為一天的開始時間計算一天的結束時間的UnixTime (因應分時區查詢)
     *
     * @param startDateTime 一天的開始時間字串
     * @return
     */
    public static String getEndOfDayString(String startDateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(startDateTime, yyyyMMddHHmmssDash);
        localDateTime = localDateTime.plusDays(1).minusSeconds(1);
        return yyyyMMddHHmmssDash.format(localDateTime);
    }

    public static String extractDate(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, yyyyMMddHHmmssDash);
        return yyyyMMddDash.format(localDateTime.toLocalDate());
    }

    private static ZoneId getDefaultZoneId(String zone) {
        try {
            return ZoneId.of(zone);
        } catch (Exception ignore){}

        return DEFAULT_ZONE_ID;
    }

    public static long millisecondToSecond(long millisecond) {
        if (MS_DIGITS == String.valueOf(millisecond).length()) {
            return millisecond/1000;
        } else {
            return millisecond;
        }
    }

    public static long secondToMillisecond(long second) {
        if (SEC_DIGITS == String.valueOf(second).length()) {
            return second * 1000;
        } else {
            return second;
        }
    }
}
