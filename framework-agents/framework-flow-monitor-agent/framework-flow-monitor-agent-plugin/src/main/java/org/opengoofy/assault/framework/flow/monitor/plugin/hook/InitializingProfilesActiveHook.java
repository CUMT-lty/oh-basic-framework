package org.opengoofy.assault.framework.flow.monitor.plugin.hook;

import org.opengoofy.assault.framework.flow.monitor.core.conf.Config;
import org.opengoofy.assault.framework.flow.monitor.plugin.toolkit.Environments;

/**
 * 初始化 `spring.profiles.active` 属性值
 */
public class InitializingProfilesActiveHook implements InitializingHook {
    
    @Override
    public void afterAgentPremain() throws Exception {
        Config.Environment.SPRING_PROFILES_ACTIVE = Environments.getSpringProfilesActive();
    }
}
