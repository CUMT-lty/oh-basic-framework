package org.opengoofy.assault.framework.starter.web.config;

import org.opengoofy.assault.framework.starter.web.GlobalExceptionHandler;
import org.opengoofy.assault.framework.starter.web.initialize.InitializeDispatcherServletController;
import org.opengoofy.assault.framework.starter.web.initialize.InitializeDispatcherServletHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Web 组件自动装配
 */
@Configuration
public class WebAutoConfiguration {
    
    public final static String INITIALIZE_PATH = "/initialize/dispatcher-servlet";
    
    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler frameworkGlobalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
    
    @Bean
    public InitializeDispatcherServletController initializeDispatcherServletController() {
        return new InitializeDispatcherServletController();
    }
    
    @Bean
    public RestTemplate simpleRestTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }
    
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);
        return factory;
    }
    
    @Bean
    public InitializeDispatcherServletHandler initializeDispatcherServletHandler(RestTemplate simpleRestTemplate, ConfigurableEnvironment configurableEnvironment) {
        return new InitializeDispatcherServletHandler(simpleRestTemplate, configurableEnvironment);
    }
}
