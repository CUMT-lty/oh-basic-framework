package org.opengoofy.assault.framework.flow.monitor.plugin.enhancer;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.opengoofy.assault.framework.flow.monitor.core.define.ClassEnhancePluginDefine;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.SPRING_CLOUD_STREAM_ROCKETMQ_CONSUMER_ENHANCE_CLASS;

/**
 * SpringCloud Stream RocketMQ 消费端切面拦截定义
 */
public final class StreamRocketMQConsumerInstrumentation implements ClassEnhancePluginDefine {
    
    private static final String ENHANCE_CLASS = SPRING_CLOUD_STREAM_ROCKETMQ_CONSUMER_ENHANCE_CLASS;
    private static final String ENHANCE_METHOD = "doInvoke";
    private static final String INTERCEPT_CLASS = "org.opengoofy.assault.framework.flow.monitor.plugin.enhancer.StreamRocketMQConsumerInterceptor";
    
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
