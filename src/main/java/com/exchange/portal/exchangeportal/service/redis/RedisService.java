package com.exchange.portal.exchangeportal.service.redis;

import com.exchange.portal.exchangeportal.common.constant.CacheTableModeEnum;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.*;

public interface RedisService {

    void cleanCachePrefix(String key);

    @SuppressWarnings("unused")
    void cleanCache(String key);

    <T> void cleanCache(String prefix, Collection<T> keys);

    @SuppressWarnings("unused")
    void cleanCache(String key, CacheTableModeEnum cleanMode);

    Boolean hasCacheKeyPrefix(String redisKey);

    @SuppressWarnings("unused")
    Boolean hasCacheKey(String redisKey);

    Boolean hasCacheHashKey(String redisKey, String hashKey);

    @SuppressWarnings("unused")
    void setCache(String key, Object value);

    void setCacheExpired(String key, Object value, Long expiredSecond);

    void setExpired(String key, Long expiredSecond);

    @SuppressWarnings("unused")
    <T> T getCacheObject(String key, TypeReference<T> valueTypeRef);

    @SuppressWarnings("unused")
    <T> T getCacheObject(String key, Class<T> objectType);

    @SuppressWarnings("unused")
    <K> void setCacheHash(String key, Map<K, ?> map);

    void setCacheSet(String redisKey, Collection<String> collection);

    <T> Map<String, T> getCacheHash(String key, TypeReference<T> valueTypeRef);

    <T> Map<String, T> getCacheHash(String key, Class<T> objectType);

    <K, V> Map<K, V> getCacheHash(String key, Class<K> keyType, TypeReference<V> valueTypeRef);

    <K, V> Map<K, V> getCacheHash(String key, Class<K> keyType, Class<V> objectType);

    @SuppressWarnings("unused")
    void setCacheHashObject(String key, String hashKey, Object value);

    @SuppressWarnings("unused")
    <T> T getCacheHashObject(String key, String hashKey, TypeReference<T> valueTypeRef);

    @SuppressWarnings("unused")
    <T> T getCacheHashObject(String key, String hashKey, Class<T> objectType);

    @SuppressWarnings("unused")
    Long getCacheHashSize(String key);

    @SuppressWarnings("unused")
    Set<String> getCacheSetMembers(String redisKey);

    <T> Set<T> getCacheSetMembers(String redisKey, Class<T> objectType);

    Set<String> getCacheDiffSet(String redisKey, Set<String> otherSet);

    <K, V> Map<K, V> getCacheMultiMap(String key, List<K> hashKeyList, Class<V> objectType);

    <K, V> Map<K, V> getCacheMultiMap(String key, List<K> hashKeyList, TypeReference<V> valueTypeRef);

    <T> List<T> getCacheMultiHash(String key, List<Object> hashKey, Class<T> objectType, CacheTableModeEnum cacheTableMode);

    <T> List<T> getCacheHashByRegex(String regexKey, Class<T> objectType);

    <T> void removeCacheMultiHash(String key, Collection<T> hashKeyList);

    <T> Set<T> getCacheHashKeySet(String key, Class<T> objectType);

    <T> boolean checkCacheSetMember(String key, T value);

    <T> Long addSetMember(String key, Set<T> values);

    <T> Long removeSetMember(String key, Set<T> values);

    <T> void removeZSetMember(String key, Set<T> values);

    <T> void pushCacheList(String key, Collection<T> values);

    <T> void rPushCacheList(String key, Collection<T> values);

    <T> T lPopCacheList(String key, Class<T> objectType);

    <T> T popFirstCacheList(String key, Class<T> objectType);

    <T> List<T> getAllCacheList(String key, Class<T> objectType);

    <T> List<T> getAllCacheList(String key, TypeReference<T> valueTypeRef);

    long countList(String key);

    <T> List<T> getLRangeListByIndex(String key, Integer firstIndex, Integer lastIndex, Class<T> objectType);

    int getListSize(String key);

    <T> void addObjToZSet(String key, T value, double score);

    <T> void addObjToSet(String key, T value);

    <T> Boolean addObjToSetIfNotExists(String key, T value);

    <T> void addListToZSet(String key, List<T> values, double score);

    <T> void addMapToZSet(String key, Map<T, Double> scoreMap);

    <T> Set<T> getZSetByScoreRange(String key, double min, double max, Class<T> objectType);

    <T> Set<T> getZSetByScoreRange(String key, double min, double max, TypeReference<T> valueTypeRef);

    <T> Map<T, Double> getZSetByScoreRangeWithScores(String key, double min, double max, long offset, long count, Class<T> objectType);

    void removeZSetByScoreRange(String key, double min, double max);

    void setMultiCache(Map<String, ?> map);

    void setMultiCache(Map<String, ?> map, Long expiredSecond);

    <T> void addZSetScore(String key, T value, double plusScore);

    <T> Map<T, Double> rangeByScoreWithScores(String key, double min, double max, Class<T> objectType);

    <K, V> void setCacheMulti(String commonKey, Map<K, V> map);

    <K, V> void setCacheMulti(String commonKey, Map<K, V> map, Long expiredSecond);

    <K, V> Map<K, V> getCacheMulti(String commonKey, List<K> keyIdList, Class<V> objectType);

    <K, V> Map<K, V> getCacheMulti(String commonKey, List<K> keyIdList, TypeReference<V> valueTypeRef);

    public List<String> getCacheByRegex(String regexKey);


    <V> V getAndSet(String key, V value, Class<V> objectType);

    <V> V getAndSet(String key, V value, TypeReference<V> valueTypeRef);
    <T> Boolean setIfAbsent(String key, T value, int expiredSec);

    <V> Map<String, Boolean> setMultiIfAbsent(String key, LinkedHashMap<String, V> valuesMap, int expiredSec);

    void setHashIncrement(String k, String hk, double value);

    void setIncrement(String k, double v);

    void setIncrement(String k, long v);

    double setHashIncrementWithReturnValue(String k, String hk, double value);

    void rename(String oldKey, String newKey);
}
