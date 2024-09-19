package org.opengoofy.assault.framework.flow.monitor.plugin.enhancer.base;

import org.opengoofy.assault.framework.flow.monitor.core.define.InstanceMethodsAroundInterceptor;
import org.opengoofy.assault.framework.flow.monitor.plugin.context.FlowMonitorRuntimeContext;

import java.lang.reflect.Method;

/**
 * 抽象类切面增强定义
 */
public abstract class AbstractInstanceMethodsAroundInterceptor implements InstanceMethodsAroundInterceptor {
    
    protected abstract void beforeMethodExecute(Object obj,
                                                Method method,
                                                Object[] allArguments,
                                                Class<?>[] argumentsTypes) throws Throwable;
    
    protected abstract void afterMethodExecute(Object obj,
                                               Method method,
                                               Object[] allArguments, Class<?>[] argumentsTypes,
                                               Object result,
                                               Throwable ex) throws Throwable;
    
    @Override
    public void beforeMethod(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
        beforeMethodExecute(obj, method, allArguments, argumentsTypes);
    }
    
    @Override
    public void afterMethod(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result, Throwable ex) throws Throwable {
        Long executeTime = FlowMonitorRuntimeContext.getExecuteTime();
        if (executeTime == null) {
            return;
        }
        afterMethodExecute(obj, method, allArguments, argumentsTypes, result, ex);
        FlowMonitorRuntimeContext.removeContent();
    }
}
