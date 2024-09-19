package org.opengoofy.assault.framework.starter.idempotent.core.spel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.opengoofy.assault.framework.starter.cache.DistributedCache;
import org.opengoofy.assault.framework.starter.idempotent.annotation.Idempotent;
import org.opengoofy.assault.framework.starter.idempotent.core.AbstractIdempotentExecuteHandler;
import org.opengoofy.assault.framework.starter.idempotent.core.IdempotentAspect;
import org.opengoofy.assault.framework.starter.idempotent.core.IdempotentContext;
import org.opengoofy.assault.framework.starter.idempotent.core.IdempotentParamWrapper;
import org.opengoofy.assault.framework.starter.idempotent.core.RepeatConsumptionException;
import org.opengoofy.assault.framework.starter.idempotent.enums.IdempotentMQConsumeStatusEnum;
import org.opengoofy.assault.framework.starter.idempotent.toolkit.LogUtil;
import org.opengoofy.assault.framework.starter.idempotent.toolkit.SpELUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 基于 SpEL 方法验证请求幂等性，适用于 MQ 场景
 */
@RequiredArgsConstructor
public final class IdempotentSpELByMQExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentSpELService {
    
    private final DistributedCache distributedCache; // 内部封装的是 redis
    
    private final static int TIMEOUT = 600;
    private final static String WRAPPER = "wrapper:spEL:MQ";
    
    @SneakyThrows
    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        Idempotent idempotent = IdempotentAspect.getIdempotent(joinPoint);
        // key：messageSendEvent.msgId+'_'+messageSendEvent.hashCode()
        String key = (String) SpELUtil.parseKey(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
        return IdempotentParamWrapper.builder().lockKey(key).joinPoint(joinPoint).build();
    }
    
    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        String uniqueKey = wrapper.getIdempotent().uniqueKeyPrefix() + wrapper.getLockKey();
        Boolean setIfAbsent = ((StringRedisTemplate) distributedCache.getInstance()) // 幂等标识是否存在
                .opsForValue()
                .setIfAbsent(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMING.getCode(), TIMEOUT, TimeUnit.SECONDS);
        if (setIfAbsent != null && !setIfAbsent) { // 幂等标识存在，说明被消费过
            String consumeStatus = distributedCache.get(uniqueKey, String.class); // 获取消费状态
            boolean error = IdempotentMQConsumeStatusEnum.isError(consumeStatus); // 是否消费成功
            LogUtil.getLog(wrapper.getJoinPoint()).warn("[{}] MQ repeated consumption, {}.", uniqueKey, error ? "Wait for the client to delay consumption" : "Status is completed");
            throw new RepeatConsumptionException(error); // 抛重复消费异常
        }
        // 幂等标识不存在
        IdempotentContext.put(WRAPPER, wrapper);
    }
    
    @Override
    public void exceptionProcessing() {
        IdempotentParamWrapper wrapper = (IdempotentParamWrapper) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            Idempotent idempotent = wrapper.getIdempotent();
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {
                distributedCache.delete(uniqueKey);
            } catch (Throwable ex) {
                LogUtil.getLog(wrapper.getJoinPoint()).error("[{}] Failed to del MQ anti-heavy token.", uniqueKey);
            }
        }
    }
    
    @Override
    public void postProcessing() {
        IdempotentParamWrapper wrapper = (IdempotentParamWrapper) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            Idempotent idempotent = wrapper.getIdempotent();
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {
                // 将消息状态设置为已消费
                distributedCache.put(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMED.getCode(), idempotent.keyTimeout(), TimeUnit.SECONDS);
            } catch (Throwable ex) {
                LogUtil.getLog(wrapper.getJoinPoint()).error("[{}] Failed to set MQ anti-heavy token.", uniqueKey);
            }
        }
    }
}
