package com.exchange.portal.exchangeportal.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Json工具
 */
@SuppressWarnings("unused")
public class JsonUtils {
    public static final String DEFAULT_TIMEZONE = "GMT-4:00";
    public static final ObjectMapper COMMON_OBJECT_MAPPER = getObjectMapper();
    public static final ObjectMapper DEFAULT_TIMEZONE_OBJECT_MAPPER = getObjectMapperWithTimeZone(DEFAULT_TIMEZONE);

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat(DateUtils.DATE_AND_TIME_DASH_FORMAT);
        objectMapper.setDateFormat(df);
        objectMapper.setTimeZone(DateUtils.DEFAULT_TIME_ZONE_OBJ);
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    private static ObjectMapper getObjectMapperWithTimeZone(String timeZone) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setTimeZone(TimeZone.getTimeZone(timeZone));

        return objectMapper;
    }

    /**
     * 物件轉字串
     *
     * @param object 物件
     * @return json 字串
     */
    public static String writeObjectAsString(Object object) {
        if (object == null)
            return null;

        try {
            return COMMON_OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Decode JSON string and parse it to indicate object.
     *
     * @param string     JSON string
     * @param objectType indicate object type
     * @param <T>        object type
     * @return indicate type object
     */
    public static <T> T readStringAsObject(String string, Class<T> objectType) {
        if (string.isEmpty()) {
            return null;
        }
        COMMON_OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try {
            return COMMON_OBJECT_MAPPER.readValue(string, objectType);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 物件轉View字串
     *
     * @param object 物件
     * @return json 字串
     */
    public static String writeObjectAsStringWithView(Object object, Class view) {
        if (object == null)
            return null;

        try {
            return COMMON_OBJECT_MAPPER.writerWithView(view).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 物件轉Base字串
     *
     * @param object 物件
     * @return json 字串
     */
    public static String writeObjectAsBase64String(Object object) {
        if (object == null)
            return null;
        String jsonString = writeObjectAsString(object);

        return Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 字串轉物件
     *
     * @param string       json 字串
     * @param valueTypeRef 型別參考
     * @param <T>          物件型別
     * @return 物件
     */
    public static <T> T readStringAsType(String string, TypeReference<T> valueTypeRef) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        try {
            return COMMON_OBJECT_MAPPER.readValue(string, valueTypeRef);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 字串轉物件
     *
     * @param string       json 字串
     * @param valueTypeRef 型別參考
     * @param <T>          物件型別
     * @return 物件
     */
    public static <T> T readStringAsTypeWithTimeZone(String string, TypeReference<T> valueTypeRef) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        try {
            return DEFAULT_TIMEZONE_OBJECT_MAPPER.readValue(string, valueTypeRef);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }


    /**
     * 將 key/value 轉成 JSON 物件
     *
     * @param values 偶數量的 array, 0, 2, 4 ... 為 key, 1, 3, 5... 為 value
     * @return json 字串
     */
    public static String toJsonString(String... values) {
        if (values == null)
            return "{}";
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "The array length is " + values.length + ", it must be even and maps to key/value pairs.");
        }

        StringBuilder sb = new StringBuilder("{");

        for (int i = 0; i < values.length; i += 2) {
            if (values[i] == null) {
                throw new IllegalArgumentException("The key cannot be null");
            }

            if (i > 0) {
                sb.append(",");
            }

            sb.append("\"" + values[i] + "\"");
            sb.append(":");

            if (values[i + 1] != null) {
                sb.append("\"" + values[i + 1] + "\"");
            } else {
                sb.append(values[i + 1]);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 將 key/value 轉成 JSON 物件, 此版本使用 + 而不使用 StringBuilder
     *
     * @param values 偶數量的 array, 0, 2, 4 ... 為 key, 1, 3, 5... 為 value
     * @return json 字串
     */
    public static String toJsonStringEx(String... values) {
        if (values == null)
            return "{}";
        if (values.length % 2 != 0)
            throw new IllegalArgumentException("The array length is " + values.length + ", it must be even and maps to key/value pairs.");

        String s = "{", prefix = "";

        for (int i = 0; i < values.length; i += 2) {
            if (values[i] == null)
                throw new IllegalArgumentException("the key cannot be null");

            s += prefix;
            s += ("\"" + values[i] + "\":");
            s += (values[i + 1] != null ? ("\"" + values[i + 1] + "\"") : (values[i + 1]));
            prefix = ",";
        }
        s += "}";

        return s;
    }

    public static String toJsonArrayString(String[]... arrays) {
        return Stream.of(arrays).map(JsonUtils::toJsonString).collect(Collectors.toList()).toString();
    }

    /**
     * 是否為 json 陣列
     *
     * @param source
     * @return
     */
    public static boolean isJsonArray(Object source) {
        if (source instanceof String == false) {
            return false;
        }
        String str = (String) source;
        try {
            JsonNode node = COMMON_OBJECT_MAPPER.readTree(str);
            return node.isArray();
        } catch (IOException e) {
            return false;
        }

    }


    public static ObjectMapper getCommonObjectMapper() {
        return COMMON_OBJECT_MAPPER;
    }
}
