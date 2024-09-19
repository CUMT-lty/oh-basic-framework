package org.opengoofy.assault.framework.flow.monitor.plugin.toolkit;

import org.opengoofy.assault.framework.flow.monitor.core.toolkit.Strings;
import org.opengoofy.assault.framework.flow.monitor.plugin.context.ApplicationContextHolderProxy;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Objects;

/**
 * 应用环境工具类
 */
public class Environments {
    
    public static String getApplicationName() {
        return ApplicationContextHolderProxy.getBean(ConfigurableEnvironment.class).getProperty("spring.application.name", "");
    }
    
    public static String getSpringProfilesActive() {
        return ApplicationContextHolderProxy.getBean(ConfigurableEnvironment.class).getProperty("spring.profiles.active", "");
    }
    
    public static String getServerServletContextPath() {
        String serverServletContextPath = ApplicationContextHolderProxy.getBean(ConfigurableEnvironment.class).getProperty("server.servlet.context-path", "");
        if (Strings.isNotEmpty(serverServletContextPath)) {
            String substring = serverServletContextPath.substring(serverServletContextPath.length() - 1, serverServletContextPath.length());
            if (Objects.equals("/", substring)) {
                serverServletContextPath = serverServletContextPath.substring(0, serverServletContextPath.length() - 1);
            }
        }
        return serverServletContextPath;
    }
}
