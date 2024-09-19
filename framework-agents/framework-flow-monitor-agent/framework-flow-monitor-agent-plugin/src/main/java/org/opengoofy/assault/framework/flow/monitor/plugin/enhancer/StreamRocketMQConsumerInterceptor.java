package org.opengoofy.assault.framework.flow.monitor.plugin.enhancer;

import lombok.SneakyThrows;
import org.opengoofy.assault.framework.flow.monitor.core.toolkit.SystemClock;
import org.opengoofy.assault.framework.flow.monitor.plugin.common.FlowMonitorFrameTypeEnum;
import org.opengoofy.assault.framework.flow.monitor.plugin.context.FlowMonitorEntity;
import org.opengoofy.assault.framework.flow.monitor.plugin.context.FlowMonitorRuntimeContext;
import org.opengoofy.assault.framework.flow.monitor.plugin.enhancer.base.AbstractInstanceMethodsAroundInterceptor;
import org.opengoofy.assault.framework.flow.monitor.plugin.provider.FlowMonitorSourceParamProviderFactory;
import org.opengoofy.assault.framework.flow.monitor.plugin.toolkit.Reflects;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SpringCloud Stream RocketMQ 生产端切面拦截增强
 */
public final class StreamRocketMQConsumerInterceptor extends AbstractInstanceMethodsAroundInterceptor {
    
    @Override
    protected void beforeMethodExecute(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
        FlowMonitorRuntimeContext.pushEnhancerType(FlowMonitorFrameTypeEnum.STREAM_ROCKETMQ_CONSUMER);
        FlowMonitorEntity sourceParam = FlowMonitorSourceParamProviderFactory.createInstance(buildKey(obj), FlowMonitorFrameTypeEnum.STREAM_ROCKETMQ_CONSUMER);
        Map<String, Map<String, FlowMonitorEntity>> applications = FlowMonitorRuntimeContext.getApplications(sourceParam.getTargetResource());
        if (applications == null) {
            Map<String, Map<String, FlowMonitorEntity>> sourceApplications = new ConcurrentHashMap<>();
            Map<String, FlowMonitorEntity> hosts = new ConcurrentHashMap<>();
            hosts.put(sourceParam.getSourceIpPort(), sourceParam);
            sourceApplications.put(sourceParam.getSourceApplication(), hosts);
            FlowMonitorRuntimeContext.putApplications(sourceParam.getTargetResource(), sourceApplications);
        }
        FlowMonitorRuntimeContext.setExecuteTime();
    }
    
    @Override
    protected void afterMethodExecute(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result, Throwable ex) throws Throwable {
        FlowMonitorEntity instance = FlowMonitorSourceParamProviderFactory.getInstance(FlowMonitorRuntimeContext.BUILD_KEY_THREADLOCAL.get(), FlowMonitorFrameTypeEnum.STREAM_ROCKETMQ_CONSUMER);
        FlowMonitorEntity sourceParam = FlowMonitorRuntimeContext.getHost(instance.getTargetResource(), instance.getSourceApplication(), instance.getSourceIpPort());
        if (ex == null) {
            sourceParam.getFlowHelper().incrSuccess(SystemClock.now() - FlowMonitorRuntimeContext.getExecuteTime());
        } else {
            sourceParam.getFlowHelper().incrException();
        }
    }
    
    @SneakyThrows
    public static String buildKey(Object obj) {
        String key = new StringBuilder("/Consumer/")
                .append(((Class) Reflects.getFieldValue(obj, "beanType")).getSimpleName())
                .append("/")
                .append(((Method) Reflects.getFieldValue(obj, "bridgedMethod")).getName())
                .toString();
        FlowMonitorRuntimeContext.BUILD_KEY_THREADLOCAL.set(key);
        return key;
    }
}
