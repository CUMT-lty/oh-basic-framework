package org.opengoofy.assault.framework.flow.monitor.plugin.common;

/**
 * 流量监控常量类
 */
public class FlowMonitorConstant {
    
    /**
     * Spring 客户端服务名
     */
    public static final String SPRING_APPLICATION_NAME = "spring.application.name";
    
    /**
     * 客户端传递 Spring 服务名 Key
     */
    public static final String SOURCE_APPLICATION_NAME = "flow.monitor.source.application.name";
    
    /**
     * 客户端传递 HTTP Method Key
     */
    public static final String SOURCE_HTTP_REQUEST_METHOD = "flow.monitor.source.request.method";
    
    /**
     * 客户端传递 HTTP URI Key
     */
    public static final String SOURCE_HTTP_REQUEST_URI = "flow.monitor.source.request.uri";
    
    /**
     * 客户端传递 Host Key
     */
    public static final String SOURCE_HTTP_HOST = "flow.monitor.source.host";
    
    /**
     * 网关调用标识
     */
    public static final String SOURCE_GATEWAY_FLAG = "flow.monitor.source.gateway.flag";
    
    /**
     * 服务端被调用 HTTP URI Key
     */
    public static final String TARGET_HTTP_REQUEST_URI = "flow.monitor.target.request.uri";
}
