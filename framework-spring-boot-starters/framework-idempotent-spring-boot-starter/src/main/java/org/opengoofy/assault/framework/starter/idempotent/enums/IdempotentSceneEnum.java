package org.opengoofy.assault.framework.starter.idempotent.enums;

/**
 * 幂等验证场景枚举
 */
public enum IdempotentSceneEnum {
    
    /**
     * 基于 RestAPI 场景验证，即防止 web rest 接口被多次调用
     */
    RESTAPI,
    
    /**
     * 基于 MQ 场景验证，即防止 MQ 中的消息被重复消费
     */
    MQ
}
