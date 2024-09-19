package org.opengoofy.assault.framework.flow.monitor.plugin.common;

import org.opengoofy.assault.framework.flow.monitor.plugin.context.ApplicationContextHolderProxy;
import org.springframework.boot.autoconfigure.web.ServerProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * 服务标识
 */
public class SID {
    
    private static String host;
    
    private static int port;
    
    static {
        InetAddress localHost;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        host = localHost.getHostAddress();
        port = Optional.ofNullable(ApplicationContextHolderProxy.getBean(ServerProperties.class).getPort()).orElse(8080);
    }
    
    public static String getIpAddressAndPort() {
        return new StringBuilder().append(host).append(":").append(port).toString();
    }
}
