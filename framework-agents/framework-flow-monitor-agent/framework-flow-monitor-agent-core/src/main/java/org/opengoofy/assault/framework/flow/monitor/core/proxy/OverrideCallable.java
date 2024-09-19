package org.opengoofy.assault.framework.flow.monitor.core.proxy;

/**
 * 覆盖可调用接口
 */
public interface OverrideCallable {
    
    /**
     * 接口调用
     *
     * @param args
     * @return
     */
    Object call(Object[] args);
}
