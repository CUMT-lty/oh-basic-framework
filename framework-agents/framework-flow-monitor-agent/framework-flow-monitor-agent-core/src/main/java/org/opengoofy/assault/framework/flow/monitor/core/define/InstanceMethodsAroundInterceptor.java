package org.opengoofy.assault.framework.flow.monitor.core.define;

import java.lang.reflect.Method;

/**
 * 类切面增强定义
 */
public interface InstanceMethodsAroundInterceptor {
    
    /**
     * 在目标方法执行前调用
     */
    void beforeMethod(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable;
    
    /**
     * 在目标方法执行后调用
     */
    void afterMethod(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result, Throwable ex) throws Throwable;
}
