package org.opengoofy.assault.framework.flow.monitor.plugin.context;

import org.opengoofy.assault.framework.flow.monitor.core.toolkit.SystemClock;
import org.opengoofy.assault.framework.flow.monitor.plugin.common.FlowMonitorFrameTypeEnum;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微服务流量监控运行上下文
 */
public final class FlowMonitorRuntimeContext {
    
    /**
     * Map<目标端URI, Map<来源应用, Map<Host, 来源应用详细信息>>>
     */
    public final static Map<String, Map<String, Map<String, FlowMonitorEntity>>> STORAGE = new ConcurrentHashMap<>();
    
    /**
     * 存储 ByteBuddy {@link @Advice.OnMethodEnter} 与 {@link @Advice.OnMethodExit}
     */
    public final static Map<FlowMonitorFrameTypeEnum, ThreadLocal<Long>> EXECUTE_TIME_THREADLOCAL = new ConcurrentHashMap<>();
    
    /**
     * 存储当前线程所执行的组件信息
     */
    public final static ThreadLocal<Stack<FlowMonitorFrameTypeEnum>> ENHANCER_TYPE_THREADLOCAL = new ThreadLocal<>();
    
    /**
     * 存储组件 Key
     */
    public final static ThreadLocal<String> BUILD_KEY_THREADLOCAL = new ThreadLocal<>();
    
    /**
     * 来源客户端所有虚拟 URI
     * <p>
     * Actual: /api/order-service/info/{orderId}
     * Virtual: /api/order-service/info/*
     * </p>
     */
    public final static Set<String> CONSUMER_ALL_VIRTUAL_URIS = new HashSet<>();
    
    /**
     * 目标客户端所有虚拟 URI
     * <p>
     * Actual: /api/message-service/info/{orderId}
     * Virtual: /api/message-service/info/*
     * </p>
     */
    public final static Set<String> PROVIDER_ALL_VIRTUAL_URIS = new HashSet<>();
    
    /**
     * 过滤 WEB 路径，比如 /error
     */
    public final static Set<String> WEB_FILTER_PATH_SETS = new HashSet<>();
    
    static {
        FlowMonitorRuntimeContext.WEB_FILTER_PATH_SETS.add("/error");
        FlowMonitorRuntimeContext.WEB_FILTER_PATH_SETS.add("/actuator/prometheus");
        Arrays.stream(FlowMonitorFrameTypeEnum.values()).forEach(each -> EXECUTE_TIME_THREADLOCAL.put(each, new ThreadLocal<>()));
    }
    
    public static void putApplications(String targetUri, Map<String, Map<String, FlowMonitorEntity>> value) {
        if (getApplications(targetUri) == null) {
            synchronized (FlowMonitorRuntimeContext.class) {
                if (getApplications(targetUri) == null) {
                    STORAGE.put(targetUri, value);
                }
            }
        }
    }
    
    public static Map<String, Map<String, FlowMonitorEntity>> getApplications(String targetUri) {
        return STORAGE.get(targetUri);
    }
    
    public static void putHosts(String targetUri, String applicationName, Map<String, FlowMonitorEntity> value) {
        if (getHosts(targetUri, applicationName) == null) {
            synchronized (FlowMonitorRuntimeContext.class) {
                if (getHosts(targetUri, applicationName) == null) {
                    STORAGE.get(targetUri).put(applicationName, value);
                }
            }
        }
    }
    
    public static Map<String, FlowMonitorEntity> getHosts(String targetUri, String applicationName) {
        return STORAGE.get(targetUri).get(applicationName);
    }
    
    public static void putHost(String targetUri, String applicationName, String host, FlowMonitorEntity param) {
        STORAGE.get(targetUri).get(applicationName).put(host, param);
    }
    
    public static FlowMonitorEntity getHost(String targetUri, String applicationName, String host) {
        return STORAGE.get(targetUri).get(applicationName).get(host);
    }
    
    public static void setExecuteTime() {
        EXECUTE_TIME_THREADLOCAL.get(peekEnhancerType()).set(SystemClock.now());
    }
    
    public static Long getExecuteTime() {
        return EXECUTE_TIME_THREADLOCAL.get(peekEnhancerType()).get();
    }
    
    public static String getConsumerVirtualUri(String actualUri) {
        return getVirtualUri(actualUri, CONSUMER_ALL_VIRTUAL_URIS);
    }
    
    public static String getProvideVirtualUri(String actualUri) {
        return getVirtualUri(actualUri, PROVIDER_ALL_VIRTUAL_URIS);
    }
    
    public static String getVirtualUri(String actualUri, Set<String> virtualUris) {
        AntPathMatcher matcher = new AntPathMatcher();
        for (String each : virtualUris) {
            if (matcher.match(each, actualUri)) {
                return each;
            }
        }
        return actualUri;
    }
    
    public static boolean hasFilterPath(String path) {
        return WEB_FILTER_PATH_SETS.contains(path);
    }
    
    public static void pushEnhancerType(FlowMonitorFrameTypeEnum flowMonitorFrameType) {
        Stack<FlowMonitorFrameTypeEnum> stack = ENHANCER_TYPE_THREADLOCAL.get();
        if (stack == null) {
            stack = new Stack<>();
        }
        stack.push(flowMonitorFrameType);
        ENHANCER_TYPE_THREADLOCAL.set(stack);
    }
    
    public static FlowMonitorFrameTypeEnum peekEnhancerType() {
        return ENHANCER_TYPE_THREADLOCAL.get().peek();
    }
    
    public static FlowMonitorFrameTypeEnum popEnhancerType() {
        return ENHANCER_TYPE_THREADLOCAL.get().pop();
    }
    
    public static void removeContent() {
        Stack<FlowMonitorFrameTypeEnum> stack = ENHANCER_TYPE_THREADLOCAL.get();
        popEnhancerType();
        if (stack.size() == 0) {
            ENHANCER_TYPE_THREADLOCAL.remove();
        }
        BUILD_KEY_THREADLOCAL.remove();
    }
}
