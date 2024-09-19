package org.opengoofy.assault.framework.flow.monitor.plugin.hook;

/**
 * 初始化钩子函数
 */
public interface InitializingHook {
    
    /**
     * AgentPremain 之后执行初始化逻辑
     */
    void afterAgentPremain() throws Exception;
}
