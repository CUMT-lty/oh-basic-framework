package org.opengoofy.assault.framework.starter.base;

import org.opengoofy.assault.framework.starter.base.init.ApplicationContentPostProcessor;
import org.opengoofy.assault.framework.starter.base.safa.FastJsonSafeMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 应用基础自动装配
 */
public class ApplicationBaseAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder congoApplicationContextHolder() {
        return new ApplicationContextHolder();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ApplicationContentPostProcessor congoApplicationContentPostProcessor() {
        return new ApplicationContentPostProcessor();
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "fastjson.safa-mode", havingValue = "true")
    public FastJsonSafeMode congoFastJsonSafeMode() {
        return new FastJsonSafeMode();
    }
}
