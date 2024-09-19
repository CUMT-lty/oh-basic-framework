package org.opengoofy.assault.framework.flow.monitor.bootstrap;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.opengoofy.assault.framework.flow.monitor.core.define.ClassEnhancePluginDefine;
import org.opengoofy.assault.framework.flow.monitor.core.define.InstanceMethodsAroundInterceptor;
import org.opengoofy.assault.framework.flow.monitor.core.loader.EnhancerInstanceLoader;
import org.opengoofy.assault.framework.flow.monitor.core.logging.Logger;
import org.opengoofy.assault.framework.flow.monitor.core.proxy.EnhancerProxy;
import org.opengoofy.assault.framework.flow.monitor.core.proxy.OverrideCallable;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.isInterface;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.nameContains;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.*;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.OPEN_FEIGN_ASPECT_CLASS;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.OPEN_FEIGN_ENHANCE_CLASS;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.SPRING_APPLICATION_ASPECT_CLASS;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.SPRING_APPLICATION_ENHANCE_CLASS;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.SPRING_CLOUD_STREAM_ROCKETMQ_CONSUMER_ASPECT_CLASS;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.SPRING_CLOUD_STREAM_ROCKETMQ_CONSUMER_ENHANCE_CLASS;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.SPRING_CLOUD_STREAM_ROCKETMQ_PROVIDER_ASPECT_CLASS;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.SPRING_CLOUD_STREAM_ROCKETMQ_PROVIDER_ENHANCE_CLASS;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.SPRING_MVC_ASPECT_CLASS;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.SPRING_MVC_ENHANCE_CLASS;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.XXL_JOB_ASPECT_CLASS;
import static org.opengoofy.assault.framework.flow.monitor.core.conf.Config.Agent.XXL_JOB_ENHANCE_CLASS;

/**
 * 微服务流量监控拦截插桩
 */
public final class FlowMonitorInterceptAgent {
    
    /**
     * 微服务流量监控插桩
     *
     * @param agentArgs       agent 传递参数
     * @param instrumentation 待处理桩
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(false));
        
        AgentBuilder agentBuilder = new AgentBuilder.Default(byteBuddy).ignore(
                nameStartsWith("net.bytebuddy.")
                        .or(nameStartsWith("org.slf4j."))
                        .or(nameStartsWith("org.groovy."))
                        .or(nameContains("javassist"))
                        .or(nameContains(".asm."))
                        .or(nameContains(".reflectasm."))
                        .or(nameStartsWith("sun.reflect"))
                        .or(ElementMatchers.isSynthetic()));
        
        for (Map.Entry<String, String> aspectEntry : loadAspectContexts().entrySet()) {
            String enhanceClass = aspectEntry.getKey();
            String enhanceAspect = aspectEntry.getValue();
            ElementMatcher.Junction matcher = named(enhanceClass).and(not(isInterface()));
            agentBuilder.type(matcher)
                    .transform(new Transformer(enhanceAspect))
                    .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                    .with(new Listener())
                    .installOn(instrumentation);
        }
    }
    
    private static Map<String, String> loadAspectContexts() {
        Map<String, String> aspectContexts = new HashMap<>();
        aspectContexts.put(SPRING_MVC_ENHANCE_CLASS, SPRING_MVC_ASPECT_CLASS);
        aspectContexts.put(XXL_JOB_ENHANCE_CLASS, XXL_JOB_ASPECT_CLASS);
        aspectContexts.put(OPEN_FEIGN_ENHANCE_CLASS, OPEN_FEIGN_ASPECT_CLASS);
        aspectContexts.put(SPRING_CLOUD_STREAM_ROCKETMQ_PROVIDER_ENHANCE_CLASS, SPRING_CLOUD_STREAM_ROCKETMQ_PROVIDER_ASPECT_CLASS);
        aspectContexts.put(SPRING_CLOUD_STREAM_ROCKETMQ_CONSUMER_ENHANCE_CLASS, SPRING_CLOUD_STREAM_ROCKETMQ_CONSUMER_ASPECT_CLASS);
        aspectContexts.put(SPRING_APPLICATION_ENHANCE_CLASS, SPRING_APPLICATION_ASPECT_CLASS);
        return aspectContexts;
    }
    
    private static class Listener implements AgentBuilder.Listener {
        
        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }
        
        @Override
        public void onTransformation(final TypeDescription typeDescription,
                                     final ClassLoader classLoader,
                                     final JavaModule module,
                                     final boolean loaded,
                                     final DynamicType dynamicType) {
            Logger.info("On Transformation class {%s}.", typeDescription.getName());
        }
        
        @Override
        public void onIgnored(final TypeDescription typeDescription,
                              final ClassLoader classLoader,
                              final JavaModule module,
                              final boolean loaded) {
        }
        
        @Override
        public void onError(final String typeName,
                            final ClassLoader classLoader,
                            final JavaModule module,
                            final boolean loaded,
                            final Throwable throwable) {
            Logger.error("Enhance class {%s} error, loaded = %s, exception msg = %s", typeName, loaded, throwable.getMessage());
        }
        
        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }
    }
    
    @RequiredArgsConstructor
    private static class Transformer implements AgentBuilder.Transformer {
        
        public final String aspectClazz;
        
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            EnhancerProxy proxy = new EnhancerProxy();
            ElementMatcher<MethodDescription> methodsMatcher = null;
            try {
                ClassEnhancePluginDefine aspectDefinition = EnhancerInstanceLoader.load(this.aspectClazz, classLoader);
                methodsMatcher = aspectDefinition.getMethodsMatcher();
                
                String enhancerClz = aspectDefinition.getMethodsEnhancer();
                InstanceMethodsAroundInterceptor enhancer = EnhancerInstanceLoader.load(enhancerClz, classLoader);
                proxy.setEnhancer(enhancer);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ignored) {
            }
            if (methodsMatcher == null) {
                return null;
            }
            ElementMatcher.Junction<MethodDescription> junction = not(isStatic()).and(methodsMatcher);
            return builder.method(junction)
                    .intercept(MethodDelegation.withDefaultConfiguration()
                            .withBinders(Morph.Binder.install(OverrideCallable.class))
                            .to(proxy));
        }
    }
}
