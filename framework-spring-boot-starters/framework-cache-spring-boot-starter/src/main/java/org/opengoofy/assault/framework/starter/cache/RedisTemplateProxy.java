package org.opengoofy.assault.framework.starter.cache;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.opengoofy.assault.framework.starter.base.Singleton;
import org.opengoofy.assault.framework.starter.cache.config.RedisDistributedProperties;
import org.opengoofy.assault.framework.starter.cache.core.CacheGetFilter;
import org.opengoofy.assault.framework.starter.cache.core.CacheGetIfAbsent;
import org.opengoofy.assault.framework.starter.cache.core.CacheLoader;
import org.opengoofy.assault.framework.starter.cache.toolkit.CacheUtil;
import org.opengoofy.assault.framework.starter.cache.toolkit.FastJson2Util;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存代理
 */
@RequiredArgsConstructor
public class RedisTemplateProxy implements DistributedCache {
    
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisDistributedProperties redisProperties;
    private final RedissonClient redissonClient;
    
    @Override
    public <T> T get(String key, Class<T> clazz) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (String.class.isAssignableFrom(clazz)) {
            return (T) value;
        }
        return JSON.parseObject(value, FastJson2Util.buildType(clazz));
    }
    
    @Override
    public void put(String key, Object value) {
        put(key, value, redisProperties.getValueTimeout());
    }
    
    @Override
    public Boolean putIfAllAbsent(@NotNull Collection<String> keys) {
        String scriptPathKey = "lua/putIfAllAbsent.lua";
        DefaultRedisScript actual = Singleton.get(scriptPathKey, () -> {
            DefaultRedisScript redisScript = new DefaultRedisScript();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(scriptPathKey)));
            redisScript.setResultType(Boolean.class);
            return redisScript;
        });
        Object result = stringRedisTemplate.execute(actual, Lists.newArrayList(keys), redisProperties.getValueTimeout().toString());
        return result == null ? false : (Boolean) result;
    }
    
    @Override
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }
    
    @Override
    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }
    
    @Override
    public <T> T get(@NotBlank String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout) {
        T result = get(key, clazz);
        if (!CacheUtil.isNullOrBlank(result)) {
            return result;
        }
        return loadAndSet(key, cacheLoader, timeout, null);
    }
    
    @Override
    public <T> T safeGet(@NotBlank String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout) {
        return safeGet(key, clazz, cacheLoader, timeout, null);
    }
    
    @Override
    public <T> T safeGet(@NotBlank String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, RBloomFilter<String> bloomFilter) {
        return safeGet(key, clazz, cacheLoader, timeout, bloomFilter, null, null);
    }
    
    @Override
    public <T> T safeGet(String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheGetFilter, CacheGetIfAbsent<String> cacheGetIfAbsent) {
        T result = get(key, clazz);
        // 缓存不等于空返回；缓存为空判断布隆过滤器是否存在，不存在返回空；如果前两者都不成立，通过函数判断是否返回空
        if (!CacheUtil.isNullOrBlank(result) || (bloomFilter != null && !bloomFilter.contains(key)) || Optional.ofNullable(cacheGetFilter).map(each -> each.filter(key)).orElse(false)) {
            return result;
        }
        RLock lock = redissonClient.getLock(CacheUtil.buildKey("distributed_lock_lock_get", key));
        lock.lock();
        try {
            // 双重判定锁，减轻数据库访问压力
            if (CacheUtil.isNullOrBlank(result = get(key, clazz))) {
                // 如果访问 load 数据为空，通过函数执行后置操作
                if (CacheUtil.isNullOrBlank(result = loadAndSet(key, cacheLoader, timeout, bloomFilter))) {
                    Optional.ofNullable(cacheGetIfAbsent).ifPresent(each -> each.execute(key));
                }
            }
        } finally {
            lock.unlock();
        }
        return result;
    }
    
    @Override
    public void put(String key, Object value, long timeout) {
        put(key, value, timeout, redisProperties.getValueTimeUnit());
    }
    
    @Override
    public void put(String key, Object value, long timeout, TimeUnit timeUnit) {
        String actual = value instanceof String ? (String) value : JSON.toJSONString(value);
        stringRedisTemplate.opsForValue().set(key, actual, timeout, timeUnit);
    }
    
    @Override
    public void safePut(String key, Object value, long timeout, RBloomFilter<String> bloomFilter) {
        safePut(key, value, timeout, redisProperties.getValueTimeUnit(), bloomFilter);
    }
    
    @Override
    public void safePut(String key, Object value, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter) {
        put(key, value, timeout, timeUnit);
        bloomFilter.add(key);
    }
    
    @Override
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }
    
    @Override
    public Object getInstance() {
        return stringRedisTemplate;
    }
    
    @Override
    public Long countExistingKeys(String... keys) {
        return stringRedisTemplate.countExistingKeys(Lists.newArrayList(keys));
    }
    
    private <T> T loadAndSet(String key, CacheLoader<T> cacheLoader, long timeout, RBloomFilter<String> bloomFilter) {
        T result = cacheLoader.load();
        if (CacheUtil.isNullOrBlank(result)) {
            return result;
        }
        if (bloomFilter != null) {
            safePut(key, result, timeout, bloomFilter);
        } else {
            put(key, result, timeout);
        }
        return result;
    }
}