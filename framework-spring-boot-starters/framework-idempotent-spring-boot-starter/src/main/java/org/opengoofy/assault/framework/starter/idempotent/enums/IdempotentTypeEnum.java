package org.opengoofy.assault.framework.starter.idempotent.enums;

/**
 * 幂等验证类型枚举
 */
public enum IdempotentTypeEnum {
    
    /**
     * 基于 Token 方式验证
     * 服务于 RESTApi 场景
     */
    TOKEN,
    
    /**
     * 基于方法参数方式验证
     * 服务于 RESTApi 场景
     */
    PARAM,
    
    /**
     * 基于 SpEL 表达式方式验证
     * 服务于 MQ 消费幂等场景
     * 消息中心中主要使用的是这种幂等验证方式
     */
    SPEL
}
