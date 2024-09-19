package org.opengoofy.assault.framework.starter.cache.core;

/**
 * 缓存查询为空
 */
@FunctionalInterface
public interface CacheGetIfAbsent<T> {
    
    /**
     * 如果查询结果为空，执行逻辑
     *
     * @param param
     */
    void execute(T param);
}
