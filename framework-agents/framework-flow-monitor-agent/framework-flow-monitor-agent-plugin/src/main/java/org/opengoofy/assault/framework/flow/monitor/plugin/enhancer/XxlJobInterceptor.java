package org.opengoofy.assault.framework.flow.monitor.plugin.enhancer;

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
 * XXL-Job 任务执行流量增强
 */
public final class XxlJobInterceptor extends AbstractInstanceMethodsAroundInterceptor {
    
    private final static String XXL_JOB_TARGET = "target";
    private final static String XXL_JOB_METHOD = "method";
    
    @Override
    public void beforeMethodExecute(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
        FlowMonitorEntity sourceParam = FlowMonitorSourceParamProviderFactory.createInstance(buildKey(obj), FlowMonitorFrameTypeEnum.XXL_JOB);
        XxlJobInterceptor.loadResource(sourceParam);
        FlowMonitorRuntimeContext.pushEnhancerType(FlowMonitorFrameTypeEnum.XXL_JOB);
        FlowMonitorRuntimeContext.setExecuteTime();
    }
    
    @Override
    public void afterMethodExecute(Object obj, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result, Throwable ex) throws Throwable {
        FlowMonitorEntity sourceParam = FlowMonitorSourceParamProviderFactory.getInstance(FlowMonitorRuntimeContext.BUILD_KEY_THREADLOCAL.get(), FlowMonitorFrameTypeEnum.XXL_JOB);
        FlowMonitorEntity flowMonitorEntity = FlowMonitorRuntimeContext.getHost(sourceParam.getTargetResource(), sourceParam.getSourceApplication(), sourceParam.getSourceIpPort());
        if (ex != null) {
            flowMonitorEntity.getFlowHelper().incrException();
        } else {
            flowMonitorEntity.getFlowHelper().incrSuccess(SystemClock.now() - FlowMonitorRuntimeContext.getExecuteTime());
        }
    }
    
    private static String buildKey(Object obj) {
        String key = new StringBuilder("/")
                .append(Reflects.getFieldValue(obj, XXL_JOB_TARGET).getClass().getSimpleName())
                .append("/")
                .append(((Method) Reflects.getFieldValue(obj, XXL_JOB_METHOD)).getName())
                .toString();
        FlowMonitorRuntimeContext.BUILD_KEY_THREADLOCAL.set(key);
        return key;
    }
    
    private static void loadResource(FlowMonitorEntity sourceParam) {
        Map<String, Map<String, FlowMonitorEntity>> applications = FlowMonitorRuntimeContext.getApplications(sourceParam.getTargetResource());
        if (applications == null) {
            Map<String, Map<String, FlowMonitorEntity>> sourceApplications = new ConcurrentHashMap<>();
            Map<String, FlowMonitorEntity> hosts = new ConcurrentHashMap<>();
            hosts.put(sourceParam.getSourceIpPort(), sourceParam);
            sourceApplications.put(sourceParam.getSourceApplication(), hosts);
            FlowMonitorRuntimeContext.putApplications(sourceParam.getTargetResource(), sourceApplications);
        }
    }
}
