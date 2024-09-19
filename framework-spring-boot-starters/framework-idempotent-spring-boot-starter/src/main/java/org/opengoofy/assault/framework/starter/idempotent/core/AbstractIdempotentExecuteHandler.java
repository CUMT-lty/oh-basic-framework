package org.opengoofy.assault.framework.starter.idempotent.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.opengoofy.assault.framework.starter.idempotent.annotation.Idempotent;

/**
 * 抽象幂等执行处理器
 */
public abstract class AbstractIdempotentExecuteHandler implements IdempotentExecuteHandler {
    
    /**
     * 构建幂等验证过程中所需要的参数包装器
     *
     * @param joinPoint AOP 方法处理
     * @return 幂等参数包装器
     */
    protected abstract IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint);
    
    /**
     * 执行幂等处理逻辑
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等注解
     */
    public void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        // 模板方法模式：构建幂等参数包装器
        IdempotentParamWrapper idempotentParamWrapper = buildWrapper(joinPoint).setIdempotent(idempotent); // 被复用的模版逻辑
        handler(idempotentParamWrapper); // 扩展的逻辑
    }
}
