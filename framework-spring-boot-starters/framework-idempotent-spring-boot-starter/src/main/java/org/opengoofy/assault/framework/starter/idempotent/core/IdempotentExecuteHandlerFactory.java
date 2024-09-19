package org.opengoofy.assault.framework.starter.idempotent.core;

import org.opengoofy.assault.framework.starter.base.ApplicationContextHolder;
import org.opengoofy.assault.framework.starter.idempotent.core.param.IdempotentParamService;
import org.opengoofy.assault.framework.starter.idempotent.core.spel.IdempotentSpELByMQExecuteHandler;
import org.opengoofy.assault.framework.starter.idempotent.core.spel.IdempotentSpELByRestAPIExecuteHandler;
import org.opengoofy.assault.framework.starter.idempotent.core.token.IdempotentTokenService;
import org.opengoofy.assault.framework.starter.idempotent.enums.IdempotentSceneEnum;
import org.opengoofy.assault.framework.starter.idempotent.enums.IdempotentTypeEnum;

/**
 * 幂等执行处理器工厂
 * <p>
 * Q：这里为什么要采用简单工厂模式？策略模式不行么？
 * A：策略模式同样可以达到获取真正幂等处理器功能。只是为了模拟更多设计模式，所以选择了简单工厂
 */
public final class IdempotentExecuteHandlerFactory {
    
    /**
     * 获取幂等执行处理器
     *
     * @param scene 指定幂等验证场景类型
     * @param type  指定幂等处理类型
     * @return 幂等执行处理器
     */
    public static IdempotentExecuteHandler getInstance(IdempotentSceneEnum scene, IdempotentTypeEnum type) {
        IdempotentExecuteHandler result = null;
        // 这里用了简单工厂模式而没有用策略模式，因为实现类是固定的，而策略模式适用于实现类不固定的
        switch (scene) {
            case RESTAPI:
                switch (type) {
                    case PARAM:
                        result = ApplicationContextHolder.getBean(IdempotentParamService.class);
                        break;
                    case TOKEN:
                        result = ApplicationContextHolder.getBean(IdempotentTokenService.class);
                        break;
                    case SPEL:
                        result = ApplicationContextHolder.getBean(IdempotentSpELByRestAPIExecuteHandler.class);
                        break;
                    default:
                        break;
                }
                break;
            case MQ:
                result = ApplicationContextHolder.getBean(IdempotentSpELByMQExecuteHandler.class); // 消息队列幂等处理器
                break;
            default:
                break;
        }
        return result;
    }
}
