package org.opengoofy.assault.framework.flow.monitor.core.toolkit;

/**
 * 提供可变值类型接口
 */
public interface Mutable<T> {
    
    /**
     * 获得原始值
     *
     * @return 原始值
     */
    T get();
    
    /**
     * 设置值
     *
     * @param value 值
     */
    void set(T value);
    
}