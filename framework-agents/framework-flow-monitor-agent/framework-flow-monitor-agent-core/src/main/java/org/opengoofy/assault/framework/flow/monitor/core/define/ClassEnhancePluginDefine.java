package org.opengoofy.assault.framework.flow.monitor.core.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 切面增强定义
 */
public interface ClassEnhancePluginDefine {
    
    /**
     * 定义需增强的类
     */
    ElementMatcher.Junction enhanceClass();
    
    /**
     * 定义需增强的方法
     */
    ElementMatcher<MethodDescription> getMethodsMatcher();
    
    /**
     * 切面增强类
     */
    String getMethodsEnhancer();
}
