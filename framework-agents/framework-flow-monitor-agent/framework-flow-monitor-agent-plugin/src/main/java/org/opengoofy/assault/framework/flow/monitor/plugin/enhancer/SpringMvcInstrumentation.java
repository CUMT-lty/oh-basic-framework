package org.opengoofy.assault.framework.flow.monitor.plugin.enhancer;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.opengoofy.assault.framework.flow.monitor.core.define.ClassEnhancePluginDefine;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.SPRING_MVC_ENHANCE_CLASS;

/**
 * Spring MVC 流量拦截
 */
public final class SpringMvcInstrumentation implements ClassEnhancePluginDefine {
    
    private static final String ENHANCE_CLASS = SPRING_MVC_ENHANCE_CLASS;
    private static final String ENHANCE_METHOD = "invokeAndHandle";
    private static final String INTERCEPT_CLASS = "org.opengoofy.assault.framework.flow.monitor.plugin.enhancer.SpringMvcInterceptor";
    
    @Override
    public ElementMatcher.Junction enhanceClass() {
        return named(ENHANCE_CLASS).and(not(isInterface()));
    }
    
    @Override
    public ElementMatcher<MethodDescription> getMethodsMatcher() {
        return named(ENHANCE_METHOD);
    }
    
    @Override
    public String getMethodsEnhancer() {
        return INTERCEPT_CLASS;
    }
}
