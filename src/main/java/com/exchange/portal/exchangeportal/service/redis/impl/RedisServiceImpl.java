package com.exchange.portal.exchangeportal.service.redis.impl;

import com.exchange.portal.exchangeportal.common.constant.CacheTableModeEnum;
import com.exchange.portal.exchangeportal.helper.RedisHelper;
import com.exchange.portal.exchangeportal.service.redis.RedisService;
import com.exchange.portal.exchangeportal.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.springframework.data.redis.core.RedisCommand.*;

/**
 * 清除redis快取
 */
@Slf4j
public class RedisServiceImpl implements RedisService {

    private static final int THOUSAND_INT = 1000;

    private static final String LAG_CHECK_SWITCH = "LAG_CHECK_SWITCH";

    private String serviceName;

    private RedisHelper redisHelper;
    private StringRedisTemplate redisTemplate;
    private StringRedisTemplate slaveRedisTemplate;

    public RedisServiceImpl(String serviceName,
                            RedisHelper redisHelper,
                            StringRedisTemplate redisTemplate,
                            StringRedisTemplate slaveRedisTemplate) {
        this.serviceName = serviceName;
        this.redisHelper = redisHelper;
        this.redisTemplate = redisTemplate;
        this.slaveRedisTemplate = slaveRedisTemplate;
    }

    @Override
    public void cleanCachePrefix(String key) {
        String pattern = getPrefix() + key + "::*";
        List<String> keys = redisHelper.keys(pattern);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.delete(keys);
        stopWatch.stop();
        lagLog(stopWatch, "cleanCachePrefix", DEL, key);
    }

    @Override
    public void cleanCache(String key) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.delete(getPrefix() + key);
        stopWatch.stop();
        lagLog(stopWatch, "cleanCache", DEL, null);
    }

    @Override
    public <T> void cleanCache(String prefix, Collection<T> keys) {
        String keyPrefix = getPrefix() + prefix + "::";
        List<String> redisKeyList = keys.stream().map(key -> keyPrefix + key).collect(Collectors.toList());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.delete(redisKeyList);
        stopWatch.stop();
        lagLog(stopWatch, "cleanCache", DEL, null);
    }

    @Override
    public void cleanCache(String key, CacheTableModeEnum cleanMode) {
        if (CacheTableModeEnum.ONLY.equals(cleanMode)) {
            cleanCache(key);
        } else {
            String pattern = getPrefix() + key + "::*";
            List<String> keys = redisHelper.keys(pattern);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            redisTemplate.delete(keys);
            stopWatch.stop();
            lagLog(stopWatch, "cleanCache", DEL, key);
        }
    }

    @Override
    public Boolean hasCacheKeyPrefix(String redisKey) {
        String pattern = getPrefix() + redisKey + "::*";
        List<String> keys = redisHelper.keys(pattern);
        return !CollectionUtils.isEmpty(keys);
    }

    @Override
    public Boolean hasCacheKey(String redisKey) {
        String key = getPrefix() + redisKey;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Boolean result = slaveRedisTemplate.hasKey(key);
        stopWatch.stop();
        lagLog(stopWatch, "hasCacheKey", EXISTS, redisKey);
        return result;
    }

    @Override
    public Boolean hasCacheHashKey(String redisKey, String hashKey) {
        String key = getPrefix() + redisKey;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean reuslt = slaveRedisTemplate.opsForHash().hasKey(key, hashKey);
        stopWatch.stop();
        lagLog(stopWatch, "hasCacheHashKey", HEXISTS, redisKey);
        return reuslt;
    }

    @Override
    public void setCache(String key, Object value) {
        String saveKey = getPrefix() + key;
        String jsonStr = JsonUtils.writeObjectAsString(value);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForValue().set(saveKey, jsonStr);
        stopWatch.stop();
        lagLog(stopWatch, "setCache", SET, key);
    }

    @Override
    public void setCacheExpired(String key, Object value, Long expiredSecond) {
        String saveKey = getPrefix() + key;
        String jsonStr = JsonUtils.writeObjectAsString(value);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForValue().set(saveKey, jsonStr, expiredSecond, TimeUnit.SECONDS);
        stopWatch.stop();
        lagLog(stopWatch, "setCacheExpired", SETEX, key);
    }

    @Override
    public void setExpired(String key, Long expiredSecond) {
        String saveKey = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.expire(saveKey, expiredSecond, TimeUnit.SECONDS);
        stopWatch.stop();
        lagLog(stopWatch, "setExpired", PEXPIRE, key);
    }

    @Override
    public <T> T getCacheObject(String key, TypeReference<T> valueTypeRef) {
        String saveKey = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String jsonStr = slaveRedisTemplate.opsForValue().get(saveKey);
        stopWatch.stop();
        lagLog(stopWatch, "getCacheObject", GET, key);
        return JsonUtils.readStringAsType(jsonStr, valueTypeRef);
    }

    @Override
    public <T> T getCacheObject(String key, Class<T> objectType) {
        String saveKey = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String jsonStr = slaveRedisTemplate.opsForValue().get(saveKey);
        stopWatch.stop();
        lagLog(stopWatch, "getCacheObject", GET, key);
        return JsonUtils.readStringAsObject(jsonStr, objectType);
    }

    @Override
    public <K> void setCacheHash(String key, Map<K, ?> map) {
        if (Objects.isNull(map))
            return;
        String saveKey = getPrefix() + key;
        Map<String, String> stringMap = new HashMap<>(map.size());
        map.forEach((k, v) -> {
                    if (Objects.isNull(v)) {
                        stringMap.put(k.toString(), "null");
                    } else {
                        stringMap.put(k.toString(), JsonUtils.writeObjectAsString(v));
                    }
                }
        );
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForHash().putAll(saveKey, stringMap);
        stopWatch.stop();
        lagLog(stopWatch, "setCacheHash", HMSET, key);
    }

    @Override
    public void setCacheSet(String redisKey, Collection<String> collection) {
        String saveKey = getPrefix() + redisKey;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.delete(saveKey);
        stopWatch.stop();
        lagLog(stopWatch, "setCacheSet", DEL, redisKey);
        stopWatch.start();
        redisTemplate.opsForSet().add(saveKey, collection.stream().map(JsonUtils::writeObjectAsString).toArray(String[]::new));
        stopWatch.stop();
        lagLog(stopWatch, "setCacheSet", SADD, redisKey);
    }

    @Override
    public <T> Map<String, T> getCacheHash(String key, TypeReference<T> valueTypeRef) {
        String saveKey = getPrefix() + key;

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Map<String, T> returnMap = Maps.newHashMap();

            slaveRedisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    Cursor<Map.Entry<Object, Object>> cursor = operations.opsForHash().scan(saveKey,
                            ScanOptions.scanOptions().match("*").count(THOUSAND_INT).build());
                    while (cursor.hasNext()) {
                        Map.Entry<Object, Object> entry = cursor.next();
                        returnMap.put(entry.getKey().toString(), JsonUtils.readStringAsType(entry.getValue().toString(), valueTypeRef));
                    }
                    return null;
                }
            });
            stopWatch.stop();
            lagLog(stopWatch, "getCacheHash", HSCAN, key);
            return returnMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    @Override
    public <T> Map<String, T> getCacheHash(String key, Class<T> objectType) {
        String saveKey = getPrefix() + key;

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Map<String, T> returnMap = Maps.newHashMap();
            slaveRedisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    Cursor<Map.Entry<Object, Object>> cursor = operations.opsForHash().scan(saveKey,
                            ScanOptions.scanOptions().match("*").count(THOUSAND_INT).build());
                    while (cursor.hasNext()) {
                        Map.Entry<Object, Object> entry = cursor.next();
                        returnMap.put(entry.getKey().toString(), JsonUtils.readStringAsObject(entry.getValue().toString(), objectType));
                    }
                    return null;
                }
            });
            stopWatch.stop();
            lagLog(stopWatch, "getCacheHash", HSCAN, key);
            return returnMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    @Override
    public <K, V> Map<K, V> getCacheHash(String key, Class<K> keyType, TypeReference<V> valueTypeRef) {
        String saveKey = getPrefix() + key;

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Map<K, V> returnMap = Maps.newHashMap();
            slaveRedisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    Cursor<Map.Entry<Object, Object>> cursor = operations.opsForHash().scan(saveKey,
                            ScanOptions.scanOptions().match("*").count(THOUSAND_INT).build());
                    while (cursor.hasNext()) {
                        Map.Entry<Object, Object> entry = cursor.next();
                        returnMap.put(JsonUtils.readStringAsObject(entry.getKey().toString(), keyType), JsonUtils.readStringAsType(entry.getValue().toString(), valueTypeRef));
                    }
                    return null;
                }
            });
            stopWatch.stop();
            lagLog(stopWatch, "getCacheHash", HSCAN, key);
            return returnMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    @Override
    public <K, V> Map<K, V> getCacheHash(String key, Class<K> keyType, Class<V> objectType) {
        String saveKey = getPrefix() + key;

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Map<K, V> returnMap = Maps.newHashMap();
            slaveRedisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    Cursor<Map.Entry<Object, Object>> cursor = operations.opsForHash().scan(saveKey,
                            ScanOptions.scanOptions().match("*").count(THOUSAND_INT).build());
                    while (cursor.hasNext()) {
                        Map.Entry<Object, Object> entry = cursor.next();
                        returnMap.put(JsonUtils.readStringAsObject(entry.getKey().toString(), keyType), JsonUtils.readStringAsObject(entry.getValue().toString(), objectType));
                    }
                    return null;
                }
            });
            stopWatch.stop();
            lagLog(stopWatch, "getCacheHash", HSCAN, key);
            return returnMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    @Override
    public void setCacheHashObject(String key, String hashKey, Object value) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String saveKey = getPrefix() + key;
        redisTemplate.opsForHash().put(saveKey, hashKey, JsonUtils.writeObjectAsString(value));
        stopWatch.stop();
        lagLog(stopWatch, "setCacheHashObject", HSET, key);
    }

    @Override
    public <T> T getCacheHashObject(String key, String hashKey, TypeReference<T> valueTypeRef) {
        Object obj = getHashObject(key, hashKey);
        return Objects.isNull(obj) ? null : JsonUtils.readStringAsType(obj.toString(), valueTypeRef);
    }

    @Override
    public <T> T getCacheHashObject(String key, String hashKey, Class<T> objectType) {
        Object obj = getHashObject(key, hashKey);
        return Objects.isNull(obj) ? null : JsonUtils.readStringAsObject(obj.toString(), objectType);
    }

    @Override
    public Long getCacheHashSize(String key) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Long l = slaveRedisTemplate.opsForHash().size(getPrefix() + key);
        stopWatch.stop();
        lagLog(stopWatch, "getCacheHashSize", HLEN, key);
        return l;
    }

    @Override
    public <T> List<T> getCacheMultiHash(String key, List<Object> hashKeyList, Class<T> objectType, CacheTableModeEnum cacheTableMode) {
        List<String> keys = new ArrayList<>();
        String getKey = getPrefix() + key;
        if (CacheTableModeEnum.ONLY.equals(cacheTableMode))
            keys.add(getKey);
        else {
            String pattern = getKey + "::*";
            keys = redisHelper.keys(pattern);
        }

        List<Object> redisHashList = new LinkedList<>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        keys.forEach((redisKey) -> {
            for (List<Object> hashKeys : Lists.partition(hashKeyList, THOUSAND_INT)) {
                redisHashList.addAll(slaveRedisTemplate.opsForHash().multiGet(redisKey, hashKeys));
            }
        });

        stopWatch.stop();
        lagLog(stopWatch, "getCacheMultiHash", HMGET, key);


        return redisHashToClassModel(redisHashList, objectType);
    }

    @Override
    public <K, V> Map<K, V> getCacheMultiMap(String key, List<K> hashKeyList, Class<V> objectType) {
        hashKeyList = hashKeyList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<Object> hashKeys = hashKeyList.stream().map(Objects::toString).collect(Collectors.toList());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Object> values = new ArrayList<>();
        for (List<Object> hashList : Lists.partition(hashKeys, THOUSAND_INT)) {
            values.addAll(slaveRedisTemplate.opsForHash().multiGet(getPrefix() + key, hashList));
        }

        stopWatch.stop();
        lagLog(stopWatch, "getCacheMultiMap", HMGET, key);
        Map<K, V> returnMap = Maps.newHashMap();
        for (int i = 0; i < hashKeys.size(); i++) {
            Object value = values.get(i);
            if (Objects.isNull(value))
                continue;

            returnMap.put(hashKeyList.get(i), JsonUtils.readStringAsObject(value.toString(), objectType));
        }
        return returnMap;
    }

    @Override
    public <K, V> Map<K, V> getCacheMultiMap(String key, List<K> hashKeyList, TypeReference<V> valueTypeRef) {
        hashKeyList = hashKeyList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<Object> hashKeys = hashKeyList.stream().map(Objects::toString).collect(Collectors.toList());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Object> values = new ArrayList<>();
        for (List<Object> hashList : Lists.partition(hashKeys, THOUSAND_INT)) {
            values.addAll(slaveRedisTemplate.opsForHash().multiGet(getPrefix() + key, hashList));
        }

        stopWatch.stop();
        lagLog(stopWatch, "getCacheMultiMap", HMGET, key);
        Map<K, V> returnMap = Maps.newHashMap();
        for (int i = 0; i < hashKeys.size(); i++) {
            Object value = values.get(i);
            if (Objects.isNull(value))
                continue;

            returnMap.put(hashKeyList.get(i), JsonUtils.readStringAsType(value.toString(), valueTypeRef));
        }
        return returnMap;
    }

    @Override
    public Set<String> getCacheSetMembers(String redisKey) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Set<String> memberSet = slaveRedisTemplate.opsForSet().members(getPrefix() + redisKey);
        stopWatch.stop();
        lagLog(stopWatch, "getCacheSetMembers", SMEMBERS, redisKey);
        return Objects.isNull(memberSet) ? Collections.emptySet() : memberSet;
    }

    @Override
    public <T> Set<T> getCacheSetMembers(String redisKey, Class<T> objectType) {
        Set<String> memberSet = getCacheSetMembers(redisKey);
        return memberSet.stream().map(member -> JsonUtils.readStringAsObject(member, objectType)).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getCacheDiffSet(String redisKey, Set<String> otherSet) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Set<String> memberSet = slaveRedisTemplate.opsForSet().members(getPrefix() + redisKey);
        stopWatch.stop();
        lagLog(stopWatch, "getCacheDiffSet", SMEMBERS, redisKey);

        if (CollectionUtils.isEmpty(memberSet))
            return Collections.emptySet();

        memberSet.removeAll(otherSet);
        return memberSet;
    }

    @Override
    //slow
    public <T> List<T> getCacheHashByRegex(String regexKey, Class<T> objectType) {
        String keyPrefix = getPrefix() + regexKey;
        List<String> keys = redisHelper.keys(keyPrefix);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Object> redisHashList = new LinkedList<>();
        keys.forEach((redisKey) ->
                redisHashList.addAll(slaveRedisTemplate.opsForHash().values(redisKey))
        );
        stopWatch.stop();
        lagLog(stopWatch, "getCacheHashByRegex", HVALS, null);
        return redisHashToClassModel(redisHashList, objectType);
    }

    @Override
    //scan
    public List<String> getCacheByRegex(String regexKey) {
        String keyPrefix = getPrefix() + regexKey;
        List<String> keys = redisHelper.keys(keyPrefix);

        List<String> redisHashList = new LinkedList<>();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (List<String> redisKey : Lists.partition(keys, THOUSAND_INT)) {
            List<String> values = slaveRedisTemplate.opsForValue().multiGet(redisKey);
            if (!CollectionUtils.isEmpty(values))
                redisHashList.addAll(values);
        }

        stopWatch.stop();
        lagLog(stopWatch, "getCacheByRegex", MGET, null);
        return redisHashList;
    }

    @Override
    public <V> V getAndSet(String key, V value, Class<V> objectType) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String oldValue = redisTemplate.opsForValue().getAndSet(keyPrefix, JsonUtils.writeObjectAsString(value));
        stopWatch.stop();
        lagLog(stopWatch, "getAndSet", GETSET, key);
        return JsonUtils.readStringAsObject(oldValue, objectType);
    }

    @Override
    public <V> V getAndSet(String key, V value, TypeReference<V> valueTypeRef) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String oldValue = redisTemplate.opsForValue().getAndSet(keyPrefix, JsonUtils.writeObjectAsString(value));
        stopWatch.stop();
        lagLog(stopWatch, "getAndSet", GETSET, key);
        return JsonUtils.readStringAsType(oldValue, valueTypeRef);
    }

    @Override
    public <T> Boolean setIfAbsent(String key, T value, int expiredSec) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean result = redisTemplate.opsForValue().setIfAbsent(getPrefix() + key, JsonUtils.writeObjectAsString(value), expiredSec, TimeUnit.SECONDS);
        stopWatch.stop();
        lagLog(stopWatch, "setIfAbsent", SETNX, key);
        return result;
    }

    @Override
    public <V> Map<String, Boolean> setMultiIfAbsent(String key, LinkedHashMap<String, V> valuesMap, int expiredSec) {
        Map<String, Boolean> resultMap = Maps.newHashMap();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Object> results = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                valuesMap.forEach((subKey, value) -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getPrefix());
                    sb.append(key);
                    sb.append("::");
                    sb.append(subKey);

                    redisOperations.opsForValue().setIfAbsent(sb.toString(), JsonUtils.writeObjectAsString(value), expiredSec, TimeUnit.SECONDS);
                });
                return null;
            }
        });
        stopWatch.stop();
        lagLog(stopWatch, "setMultiIfAbsent", SETNX, key);

        if (CollectionUtils.isEmpty(results))
            return resultMap;

        AtomicInteger i = new AtomicInteger();
        valuesMap.forEach((k, v) -> {
            resultMap.put(k, (Boolean) results.get(i.get()));
            i.getAndIncrement();
        });

        return resultMap;
    }

    @Override
    public void setHashIncrement(String k, String hk, double value) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForHash().increment(getPrefix() + k, hk, value);
        stopWatch.stop();
        lagLog(stopWatch, "setHashIncrement", HINCRBY, k);
    }

    @Override
    public void setIncrement(String k, double v) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForValue().increment(getPrefix() + k, v);
        stopWatch.stop();
        lagLog(stopWatch, "setIncrement-double", HINCRBY, k);
    }

    @Override
    public void setIncrement(String k, long v) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForValue().increment(getPrefix() + k, v);
        stopWatch.stop();
        lagLog(stopWatch, "setIncrement-long", INCR, k);
    }

    @Override
    public double setHashIncrementWithReturnValue(String k, String hk, double value) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Double res = redisTemplate.opsForHash().increment(getPrefix() + k, hk, value);
        stopWatch.stop();
        lagLog(stopWatch, "setHashIncrement", INCR, k);
        return res;
    }

    @Override
    public <T> void removeCacheMultiHash(String key, Collection<T> hashKeyList) {
        if (CollectionUtils.isEmpty(hashKeyList))
            return;

        String keyPrefix = getPrefix() + key;
        Object[] removeHashKeyList = hashKeyList.stream().map(Object::toString).toArray(String[]::new);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForHash().delete(keyPrefix, removeHashKeyList);
        stopWatch.stop();
        lagLog(stopWatch, "removeCacheMultiHash", HDEL, key);
    }

    @Override
    public <T> Set<T> getCacheHashKeySet(String key, Class<T> objectType) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Set<Object> data = slaveRedisTemplate.opsForHash().keys(keyPrefix);
        stopWatch.stop();
        lagLog(stopWatch, "getCacheHashKeySet", HKEYS, key);
        Set<T> result = new HashSet<>();
        data.forEach((item) -> result.add(JsonUtils.readStringAsObject(item.toString(), objectType)));
        return result.isEmpty() ? Collections.emptySet() : result;
    }

    public <T> boolean checkCacheSetMember(String key, T value) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Boolean isExistValue = slaveRedisTemplate.opsForSet().isMember(keyPrefix, JsonUtils.writeObjectAsString(value));
        stopWatch.stop();
        lagLog(stopWatch, "checkCacheSetMember", SISMEMBER, key);
        return Objects.isNull(isExistValue) ? false : isExistValue;
    }

    @Override
    public <T> Long addSetMember(String key, Set<T> values) {
        if (CollectionUtils.isEmpty(values))
            return 0L;

        String keyPrefix = getPrefix() + key;
        String[] valueArray = values.stream().map(JsonUtils::writeObjectAsString).toArray(String[]::new);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Long l = redisTemplate.opsForSet().add(keyPrefix, valueArray);
        stopWatch.stop();
        lagLog(stopWatch, "addSetMember", SADD, key);
        return l;
    }

    @Override
    public <T> Long removeSetMember(String key, Set<T> values) {
        if (CollectionUtils.isEmpty(values))
            return 0L;

        String keyPrefix = getPrefix() + key;
        String[] valueArray = values.stream().map(JsonUtils::writeObjectAsString).toArray(String[]::new);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Long l = redisTemplate.opsForSet().remove(keyPrefix, valueArray);
        stopWatch.stop();
        lagLog(stopWatch, "removeSetMember", SREM, key);
        return l;
    }

    public <T> void removeZSetMember(String key, Set<T> values) {
        if (CollectionUtils.isEmpty(values))
            return;

        String ketPrefix = getPrefix() + key;
        String[] valueArray = values.stream().map(JsonUtils::writeObjectAsString).toArray(String[]::new);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForZSet().remove(ketPrefix, valueArray);
        stopWatch.stop();
        lagLog(stopWatch, "removeZSetMember", ZREM, key);
    }

    @Override
    public <T> void pushCacheList(String key, Collection<T> values) {
        if (CollectionUtils.isEmpty(values))
            return;

        String keyPrefix = getPrefix() + key;
        List<String> redisValues = values.stream().map(JsonUtils::writeObjectAsString).collect(Collectors.toList());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForList().leftPushAll(keyPrefix, redisValues);
        stopWatch.stop();
        lagLog(stopWatch, "pushCacheList", LPUSH, key);
    }

    @Override
    public <T> void rPushCacheList(String key, Collection<T> values) {
        if (CollectionUtils.isEmpty(values))
            return;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String keyPrefix = getPrefix() + key;
        List<String> redisValues = values.stream().map(JsonUtils::writeObjectAsString).collect(Collectors.toList());
        redisTemplate.opsForList().rightPushAll(keyPrefix, redisValues);
        stopWatch.stop();
        lagLog(stopWatch, "rPushCacheList", RPUSH, key);
    }

    @Override
    public <T> T lPopCacheList(String key, Class<T> objectType) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String keyPrefix = getPrefix() + key;
        String str = redisTemplate.opsForList().leftPop(keyPrefix);

        stopWatch.stop();
        lagLog(stopWatch, "lPopCacheList", LPOP, key);
        return JsonUtils.readStringAsObject(str, objectType);
    }

    @Override
    public <T> T popFirstCacheList(String key, Class<T> objectType) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String keyPrefix = getPrefix() + key;
        String str = redisTemplate.opsForList().rightPop(keyPrefix);
        stopWatch.stop();
        lagLog(stopWatch, "popFirstCacheList", RPOP, key);
        return JsonUtils.readStringAsObject(str, objectType);
    }

    @Override
    public <T> List<T> getAllCacheList(String key, Class<T> objectType) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 如果结束位是-1， 则表示取所有的值
        List<String> redisValues = slaveRedisTemplate.opsForList().range(keyPrefix, 0, -1);
        stopWatch.stop();
        lagLog(stopWatch, "getAllCacheList", LRANGE, key);
        if (CollectionUtils.isEmpty(redisValues))
            return Collections.emptyList();

        return redisValues.stream().map(str -> JsonUtils.readStringAsObject(str, objectType)).collect(Collectors.toList());
    }

    @Override
    public <T> List<T> getAllCacheList(String key, TypeReference<T> valueTypeRef) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 如果结束位是-1， 则表示取所有的值
        List<String> redisValues = slaveRedisTemplate.opsForList().range(keyPrefix, 0, -1);

        stopWatch.stop();
        lagLog(stopWatch, "getAllCacheList", LRANGE, key);
        if (CollectionUtils.isEmpty(redisValues))
            return Collections.emptyList();

        List<T> returnList = new ArrayList<>(redisValues.size());
        redisValues.forEach(str ->
                returnList.add(JsonUtils.readStringAsType(str, valueTypeRef))
        );
        return returnList;
    }

    @Override
    public long countList(String key) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Long size = slaveRedisTemplate.opsForList().size(keyPrefix);
        stopWatch.stop();
        lagLog(stopWatch, "countList", LLEN, key);
        return size;
    }

    @Override
    public <T> List<T> getLRangeListByIndex(String key, Integer firstIndex, Integer lastIndex, Class<T> objectType) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 如果结束位是-1， 则表示取所有的值
        List<String> redisValues = slaveRedisTemplate.opsForList().range(keyPrefix, firstIndex, lastIndex);
        stopWatch.stop();
        lagLog(stopWatch, "getLRangeListByIndex", LRANGE, key);
        if (CollectionUtils.isEmpty(redisValues))
            return Collections.emptyList();

        return redisValues.stream().map(str -> JsonUtils.readStringAsObject(str, objectType)).collect(Collectors.toList());
    }

    @Override
    public int getListSize(String key) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Long size = slaveRedisTemplate.opsForList().size(keyPrefix);

        stopWatch.stop();
        lagLog(stopWatch, "getListSize", LLEN, key);
        return Objects.isNull(size) ? 0 : size.intValue();
    }

    @Override
    public <T> void addObjToZSet(String key, T value, double score) {
        String keyPrefix = getPrefix() + key;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        redisTemplate.opsForZSet().add(keyPrefix, JsonUtils.writeObjectAsString(value), score);

        stopWatch.stop();
        lagLog(stopWatch, "addObjToZSet", ZADD, key);
    }

    @Override
    public <T> void addObjToSet(String key, T value) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForSet().add(keyPrefix, JsonUtils.writeObjectAsString(value));
        stopWatch.stop();
        lagLog(stopWatch, "addObjToSet", SADD, key);
    }

    @Override
    public <T> Boolean addObjToSetIfNotExists(String key, T value) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Boolean result = redisTemplate.opsForSet().add(keyPrefix, JsonUtils.writeObjectAsString(value)) != 0;
        stopWatch.stop();
        lagLog(stopWatch, "addObjToSetIfNotExists", SADD, key);
        return result;
    }

    @Override
    public <T> void addListToZSet(String key, List<T> values, double score) {
        if (CollectionUtils.isEmpty(values))
            return;

        String keyPrefix = getPrefix() + key;
        Set<TypedTuple<String>> set = values.stream()
                .map(obj -> new DefaultTypedTuple<>(JsonUtils.writeObjectAsString(obj), score))
                .collect(Collectors.toSet());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForZSet().add(keyPrefix, set);
        stopWatch.stop();
        lagLog(stopWatch, "addListToZSet", ZADD, key);
    }

    @Override
    public <T> void addMapToZSet(String key, Map<T, Double> scoreMap) {
        Set<TypedTuple<String>> tuples = scoreMap.entrySet().stream()
                .map(entry -> new DefaultTypedTuple<>(JsonUtils.writeObjectAsString(entry.getKey()), entry.getValue()))
                .collect(Collectors.toSet());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForZSet().add(getPrefix() + key, tuples);
        stopWatch.stop();
        lagLog(stopWatch, "addMapToZSet", ZADD, key);
    }

    @Override
    public <T> Set<T> getZSetByScoreRange(String key, double min, double max, Class<T> objectType) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String keyPrefix = getPrefix() + key;
        Set<String> redisValues = slaveRedisTemplate.opsForZSet().rangeByScore(keyPrefix, min, max);
        stopWatch.stop();
        lagLog(stopWatch, "getZSetByScoreRange", ZRANGEBYSCORE, key);
        if (CollectionUtils.isEmpty(redisValues))
            return Collections.emptySet();

        return redisValues.stream().map(str -> JsonUtils.readStringAsObject(str, objectType)).collect(Collectors.toSet());
    }

    @Override
    public <T> Set<T> getZSetByScoreRange(String key, double min, double max, TypeReference<T> valueTypeRef) {
        String keyPrefix = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Set<String> redisValues = slaveRedisTemplate.opsForZSet().rangeByScore(keyPrefix, min, max);
        stopWatch.stop();
        lagLog(stopWatch, "getZSetByScoreRange", ZRANGEBYSCORE, key);
        if (CollectionUtils.isEmpty(redisValues))
            return Collections.emptySet();

        Set<T> returnSet = Sets.newHashSet();
        redisValues.forEach(str ->
                returnSet.add(JsonUtils.readStringAsType(str, valueTypeRef))
        );
        return returnSet;
    }

    @Override
    public <T> Map<T, Double> getZSetByScoreRangeWithScores(String key, double min, double max, long offSet, long count, Class<T> objectType) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Set<TypedTuple<String>> tuples = slaveRedisTemplate.opsForZSet().rangeByScoreWithScores(getPrefix() + key, min, max, offSet, count);

        stopWatch.stop();
        lagLog(stopWatch, "getZSetByScoreRangeWithScores", ZRANGEBYSCORE, key);

        Map<T, Double> redisMap = new HashMap<>();
        if (CollectionUtils.isEmpty(tuples))
            return Collections.emptyMap();

        tuples.forEach(tuple -> redisMap.put(JsonUtils.readStringAsObject(tuple.getValue(), objectType), tuple.getScore()));

        return redisMap;
    }

    @Override
    public void setMultiCache(Map<String, ?> map) {
        if (CollectionUtils.isEmpty(map))
            return;
        Map<String, String> stringMap = new HashMap<>(map.size());

        map.forEach((k, v) -> stringMap.put(getPrefix() + k, JsonUtils.writeObjectAsString(v)));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        redisTemplate.opsForValue().multiSet(stringMap);

        stopWatch.stop();
        lagLog(stopWatch, "setMultiCache", SET, null);
    }

    @Override
    public void setMultiCache(Map<String, ?> map, Long expiredSecond) {
        if (CollectionUtils.isEmpty(map))
            return;
        setMultiCache(map);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CompletableFuture.runAsync(() ->
                        map.keySet().forEach(k -> setExpired(k, expiredSecond)))
                .thenRun(() -> {
                    stopWatch.stop();
                    lagLog(stopWatch, "setMultiCache(setExpired)", SETEX, null);
                });

    }

    @Override
    public void removeZSetByScoreRange(String key, double min, double max) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String keyPrefix = getPrefix() + key;
        redisTemplate.opsForZSet().removeRangeByScore(keyPrefix, min, max);
        stopWatch.stop();
        lagLog(stopWatch, "removeZSetByScoreRange", ZREMRANGEBYSCORE, key);
    }

    @Override
    public <T> void addZSetScore(String key, T value, double plusScore) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForZSet().incrementScore(getPrefix() + key, JsonUtils.writeObjectAsString(value), plusScore);
        stopWatch.stop();
        lagLog(stopWatch, "addZSetScore", ZINCRBY, key);
    }

    @Override
    public <T> Map<T, Double> rangeByScoreWithScores(String key, double min, double max, Class<T> objectType) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Set<TypedTuple<String>> tuples = slaveRedisTemplate.opsForZSet().rangeByScoreWithScores(getPrefix() + key, min, max);
        Map<T, Double> redisMap = new HashMap<>();
        if (CollectionUtils.isEmpty(tuples))
            return Collections.emptyMap();

        tuples.forEach(tuple -> redisMap.put(JsonUtils.readStringAsObject(tuple.getValue(), objectType), tuple.getScore()));
        stopWatch.stop();
        lagLog(stopWatch, "rangeByScoreWithScores", ZRANGEBYSCORE, key);
        return redisMap;
    }

    @Override
    public <K, V> void setCacheMulti(String commonKey, Map<K, V> map) {
        String prefixKey = getPrefix() + commonKey;
        Map<String, String> redisValues = new HashMap<>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        map.forEach((k, v) -> {
            StringBuilder keyBuilder = new StringBuilder(prefixKey).append("::");
            redisValues.put(keyBuilder.append(k).toString(), JsonUtils.writeObjectAsString(v));
        });
        stopWatch.stop();
        redisTemplate.opsForValue().multiSet(redisValues);
        lagLog(stopWatch, "setCacheMulti", MSET, commonKey);
    }

    @Override
    public <K, V> void setCacheMulti(String commonKey, Map<K, V> map, Long expiredSecond) {
        StopWatch stopWatch = new StopWatch();
        String prefixKey = getPrefix() + commonKey;
        Map<String, String> redisValues = new HashMap<>();
        stopWatch.start();
        map.forEach((k, v) -> {
            StringBuilder keyBuilder = new StringBuilder(prefixKey).append("::");
            redisValues.put(keyBuilder.append(k).toString(), JsonUtils.writeObjectAsString(v));
        });
        redisTemplate.opsForValue().multiSet(redisValues);
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisValues.keySet().forEach(key -> redisOperations.expire(key, expiredSecond, TimeUnit.SECONDS));
                return null;
            }
        });
        stopWatch.stop();
        lagLog(stopWatch, "setCacheMulti", MSET, commonKey);
    }

    @Override
    public <K, V> Map<K, V> getCacheMulti(String commonKey, List<K> keyIdList, Class<V> objectType) {
        StopWatch stopWatch = new StopWatch();
        String prefixKey = getPrefix() + commonKey;
        stopWatch.start();
        List<String> allRedisKeys = redisHelper.multiKeys(prefixKey, keyIdList);

        List<String> values = slaveRedisTemplate.opsForValue().multiGet(allRedisKeys);
        stopWatch.stop();
        lagLog(stopWatch, "getCacheMulti", MGET, commonKey);
        Map<K, V> returnMap = Maps.newHashMap();
        for (int i = 0; i < keyIdList.size(); i++) {
            String value = values.get(i);
            if (Strings.isEmpty(value))
                continue;
            returnMap.put(keyIdList.get(i), JsonUtils.readStringAsObject(value, objectType));
        }
        return returnMap;
    }

    @Override
    public <K, V> Map<K, V> getCacheMulti(String commonKey, List<K> keyIdList, TypeReference<V> valueTypeRef) {
        StopWatch stopWatch = new StopWatch();
        String prefixKey = getPrefix() + commonKey;
        stopWatch.start();
        List<String> allRedisKeys = redisHelper.multiKeys(prefixKey, keyIdList);

        List<String> values = slaveRedisTemplate.opsForValue().multiGet(allRedisKeys);
        stopWatch.stop();
        lagLog(stopWatch, "getCacheMulti", MGET, commonKey);

        Map<K, V> returnMap = Maps.newHashMap();
        for (int i = 0; i < keyIdList.size(); i++) {
            String value = values.get(i);
            if (Strings.isEmpty(value))
                continue;
            returnMap.put(keyIdList.get(i), JsonUtils.readStringAsType(value, valueTypeRef));
        }
        return returnMap;
    }

    @Override
    public void rename(String oldKey, String newKey) {
        if (Strings.isNotEmpty(oldKey) && Strings.isNotEmpty(newKey)) {
            oldKey = getPrefix() + oldKey;
            newKey = getPrefix() + newKey;
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            redisTemplate.rename(oldKey, newKey);
            stopWatch.stop();
            lagLog(stopWatch, "rename", RENAME, String.format("rename key : %s to : %s", oldKey, newKey));
        }

    }

    private String getPrefix() {
        return CacheKeyPrefix.simple().compute(serviceName);
    }

    private Object getHashObject(String key, String hashKey) {
        String saveKey = getPrefix() + key;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object obj = slaveRedisTemplate.opsForHash().get(saveKey, hashKey);

        stopWatch.stop();
        lagLog(stopWatch, "getHashObject", HGET, key, ImmutableMap.of("hashKey", hashKey));
        return obj;
    }

    private <T> List<T> redisHashToClassModel(List<Object> redisHashList, Class<T> objectType) {
        if (CollectionUtils.isEmpty(redisHashList))
            return Collections.emptyList();

        List<T> resultList = new ArrayList<>(redisHashList.size());
        redisHashList.forEach((item) -> {
            if (Objects.nonNull(item))
                resultList.add(JsonUtils.readStringAsObject(item.toString(), objectType));
        });

        return resultList;
    }

    private void lagLog(StopWatch stopWatch, String action, RedisCommand type, String key) {
        lagLog(stopWatch, action, type, key, null);
    }

    private void lagLog(StopWatch stopWatch, String action, RedisCommand type, String key, Map<String, Object> others) {
        long one_sec = 1000L;
        String lagCheckSwitch = redisTemplate.opsForValue().get(LAG_CHECK_SWITCH);
        if (stopWatch.getLastTaskTimeMillis() < one_sec)
            return;

        log.info(JsonUtils.writeObjectAsString(one_sec));
    }
}
