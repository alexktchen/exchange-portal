package com.exchange.portal.exchangeportal.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@Slf4j
@Component
public class RedisHelper {

    private static final long SCAN_COUNT = 1000;
    private StringRedisTemplate redisTemplate;

    @Autowired
    public RedisHelper(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private void scan(String pattern, Consumer<byte[]> consumer) {
        this.redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(SCAN_COUNT).match(pattern).build())) {
                cursor.forEachRemaining(consumer);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    public List<String> keys(String pattern) {
        List<String> keys = new ArrayList<>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        this.scan(pattern, item -> {
            //符合條件的key
            String key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        stopWatch.stop();
        return keys;
    }

    public List<String> multiKeys(String prefix, List<?> keyIdList) {
        List<String> allRedisKeys = new ArrayList<>();
        for (Object keyId : keyIdList) {
            StringBuilder builder = new StringBuilder();
            builder.append(prefix).append("::").append(keyId);
            allRedisKeys.add(builder.toString());
        }
        return allRedisKeys;
    }
}
