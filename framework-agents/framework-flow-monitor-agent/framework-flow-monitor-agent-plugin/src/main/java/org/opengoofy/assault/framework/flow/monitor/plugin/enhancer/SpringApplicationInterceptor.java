package org.opengoofy.assault.framework.flow.monitor.plugin.enhancer;

import org.opengoofy.assault.framework.flow.monitor.core.define.InstanceMethodsAroundInterceptor;
import org.opengoofy.assault.framework.flow.monitor.plugin.context.ApplicationContextHolderProxy;
import org.opengoofy.assault.framework.flow.monitor.plugin.context.FlowMonitorVirtualUriLoader;
import org.opengoofy.assault.framework.flow.monitor.plugin.hook.InitializingHookManager;
import org.opengoofy.assault.framework.flow.monitor.plugin.writer.FlowMonitorWrite;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;

/**
 * SpringApplication 增强，获取 Spring 应用上下文
 */
public final class SpringApplicationInterceptor implements InstanceMethodsAroundInterceptor {
    
    private final String APPLICATION_CONTEXT_CLASS_NAME = "org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext";
    
    @Override
    public void beforeMethod(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
        
    }
    
    @Override
    public void afterMethod(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result, Throwable ex) throws Throwable {
        if (result != null && result.getClass().getName().equals(APPLICATION_CONTEXT_CLASS_NAME)) {
            ApplicationContextHolderProxy.initContext((ConfigurableApplicationContext) result);
            FlowMonitorVirtualUriLoader.loadConsumerUris();
            FlowMonitorVirtualUriLoader.loadProviderUris();
            FlowMonitorWrite.initScheduleWriteData();
            InitializingHookManager.INSTANCE.boot();
        }
    }
}
