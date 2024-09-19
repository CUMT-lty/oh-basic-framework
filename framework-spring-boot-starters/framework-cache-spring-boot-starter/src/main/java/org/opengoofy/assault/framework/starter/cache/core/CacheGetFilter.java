package org.opengoofy.assault.framework.starter.cache.core;

/**
 * 缓存过滤
 */
@FunctionalInterface
public interface CacheGetFilter<T> {
    
    /**
     * 缓存过滤
     *
     * @param param 输出参数
     * @return {@code true} 如果输入参数匹配，否则 {@code false}
     */
    boolean filter(T param);
}
