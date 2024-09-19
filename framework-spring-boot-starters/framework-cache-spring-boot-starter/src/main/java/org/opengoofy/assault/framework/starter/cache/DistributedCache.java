package org.opengoofy.assault.framework.starter.cache;

import org.opengoofy.assault.framework.starter.cache.core.CacheGetFilter;
import org.opengoofy.assault.framework.starter.cache.core.CacheGetIfAbsent;
import org.opengoofy.assault.framework.starter.cache.core.CacheLoader;
import org.redisson.api.RBloomFilter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

/**
 * 分布式缓存
 */
public interface DistributedCache extends Cache {
    
    /**
     * 获取缓存，如查询结果为空，调用 cacheLoader 加载缓存
     */
    <T> T get(@NotBlank String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout);
    
    /**
     * 以一种"安全"的方式获取缓存，如查询结果为空，调用 cacheLoader 加载缓存
     */
    <T> T safeGet(@NotBlank String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout);
    
    /**
     * 以一种"安全"的方式获取缓存，如查询结果为空，调用 cacheLoader 加载缓存
     */
    <T> T safeGet(@NotBlank String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, RBloomFilter<String> bloomFilter);
    
    /**
     * 以一种"安全"的方式获取缓存，如查询结果为空，调用 cacheLoader 加载缓存
     */
    <T> T safeGet(@NotBlank String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout,
                  RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheCheckFilter, CacheGetIfAbsent<String> cacheGetIfAbsent);
    
    /**
     * 放入缓存，自定义超时时间
     */
    void put(@NotBlank String key, Object value, long timeout);
    
    /**
     * 放入缓存，自定义超时时间
     */
    void put(@NotBlank String key, Object value, long timeout, TimeUnit timeUnit);
    
    /**
     * 放入缓存，自定义超时时间
     * 并将 key 加入步隆过滤器，配置 {@link DistributedCache#safeGet(String, Class, CacheLoader, long)}
     */
    void safePut(@NotBlank String key, Object value, long timeout, RBloomFilter<String> bloomFilter);
    
    /**
     * 放入缓存，自定义超时时间
     * 并将 key 加入步隆过滤器，配置 {@link DistributedCache#safeGet(String, Class, CacheLoader, long)}
     */
    void safePut(@NotBlank String key, Object value, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter);
    
    /**
     * 统计指定 key 的存在数量
     */
    Long countExistingKeys(@NotNull String... keys);
}
