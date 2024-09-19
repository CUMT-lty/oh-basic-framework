package org.opengoofy.assault.framework.starter.idempotent.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.opengoofy.assault.framework.starter.idempotent.annotation.Idempotent;

import java.lang.reflect.Method;

/**
 * 幂等注解 AOP 拦截器
 */
@Aspect
public final class IdempotentAspect {
    
    /**
     * 增强方法标记 {@link Idempotent} 注解逻辑
     */
    @Around("@annotation(org.opengoofy.assault.framework.starter.idempotent.annotation.Idempotent)") // 拦截这个注解
    public Object idempotentHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Idempotent idempotent = getIdempotent(joinPoint); // 获取注解
        // 根据幂等注解配置获取相应的幂等处理器，简单工厂模式
        IdempotentExecuteHandler instance = IdempotentExecuteHandlerFactory.getInstance(idempotent.scene(), idempotent.type());
        Object resultObj;
        try {
            instance.execute(joinPoint, idempotent); // 执行幂等处理逻辑，执行失败会抛异常
            resultObj = joinPoint.proceed();
            instance.postProcessing(); // AOP 后置处理
        } catch (RepeatConsumptionException ex) { // 处理重复消费异常
            /**
             * 触发幂等逻辑时可能有两种情况：
             *    * 1. 消息还在处理，但是不确定是否执行成功，那么需要返回错误，方便 RocketMQ 再次通过重试队列投递
             *    * 2. 消息处理成功了，该消息直接返回成功即可
             */
            if (!ex.getError()) { // 如果 error 标识为 true
                return null; // rocketmq 认为消息处理成功
            }
            throw ex; // 如果 error 标识为 false，该异常将被抛给 rocketmq 的 broker，告诉 MQ 消息消费失败，会触发 MQ 的重试。
        } catch (Throwable ex) {
            // 客户端消费存在异常，需要删除幂等标识方便下次 RocketMQ 再次通过重试队列投递
            instance.exceptionProcessing();
            throw ex;
        } finally {
            IdempotentContext.clean();
        }
        return resultObj;
    }
    
    public static Idempotent getIdempotent(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        return targetMethod.getAnnotation(Idempotent.class);
    }
}
